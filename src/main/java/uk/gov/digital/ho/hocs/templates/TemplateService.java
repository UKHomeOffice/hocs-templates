package uk.gov.digital.ho.hocs.templates;

import lombok.extern.slf4j.Slf4j;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.CaseworkClient;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.AddressDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CaseDataDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentsDto;
import uk.gov.digital.ho.hocs.templates.client.documentclient.DocumentClient;
import uk.gov.digital.ho.hocs.templates.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.templates.client.infoclient.dto.TeamDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.*;

@Slf4j
@Service
public class TemplateService {

    private static final String CONSTITUENT = "CONSTITUENT";
    private final CaseworkClient caseworkClient;
    private final InfoClient infoClient;
    private final DocumentClient documentClient;

    @Autowired
    public TemplateService(CaseworkClient caseworkClient,
                           InfoClient infoClient,
                           DocumentClient documentClient) {
        this.caseworkClient = caseworkClient;
        this.infoClient = infoClient;
        this.documentClient = documentClient;
    }

    public TemplateResult buildTemplate(UUID caseUUID, UUID templateUUID) {

        CaseDataDto caseDetails = caseworkClient.getCase(caseUUID);

        CorrespondentsDto correspondents = caseworkClient.getCorrespondents(caseUUID);

        CorrespondentDto primary = correspondents.getCorrespondents().stream().
                filter(p -> p.getUuid().equals(caseDetails.getPrimaryCorrespondent())).
                findFirst().orElse(new CorrespondentDto(new AddressDto()));

        CorrespondentDto constituent = correspondents.getCorrespondents().stream().
                filter(p -> p.getType().equals(CONSTITUENT)).
                findFirst().orElse(new CorrespondentDto(new AddressDto()));

        TeamDto team = getTeamDetails(caseUUID, caseDetails);

        HashMap<String, String> variables = createVariablesMap(caseDetails, primary, constituent, team);
        variables.replaceAll((k, v) -> HtmlUtils.htmlEscape(v));

        InputStream templateInputStream = getTemplateAsInputStream(templateUUID);

        byte[] generatedTemplate = null;
        try {
            generatedTemplate = generateTemplate(variables, templateInputStream);
        } catch (Exception e) {
            log.error("Generate Template Exception: {}", e.getMessage(), value(EVENT, TEMPLATE_GENERATION_FAILURE));
        }

        return new TemplateResult(caseDetails.getReference(), generatedTemplate);
    }


    private TeamDto getTeamDetails(UUID caseUUID, CaseDataDto caseDetails) {
        TeamDto team = new TeamDto();
        String stageType = null;
        try {
            stageType = "DCU_" + caseDetails.getType() + "_PRIVATE_OFFICE";
            team = infoClient.getTeamForTopicAndStage(caseUUID, caseDetails.getPrimaryTopic(), stageType);
        } catch (Exception e) {
            log.error("Info Service could not get Team letter name for {}", stageType, value(EVENT, INFO_CLIENT_GET_TEAM_NOT_FOUND));
        }
        return team;
    }

    private String formatDate(String unformattedDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        LocalDate date = LocalDate.parse(unformattedDate);
        return date.format(dateTimeFormatter);
    }

    private InputStream getTemplateAsInputStream(UUID templateUUID) {

        ByteArrayResource templateByteArrayResource = documentClient.getTemplate(templateUUID);
        return new ByteArrayInputStream(templateByteArrayResource.getByteArray());
    }

    private HashMap<String, String> createVariablesMap(CaseDataDto caseDetails, CorrespondentDto primary, CorrespondentDto constituent, TeamDto team) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("primaryCorrespondentName", Objects.toString(primary.getFullname(), ""));
        variables.put("primaryCorrespondentAddress1", Objects.toString(primary.getAddress().getAddress1(), ""));
        variables.put("primaryCorrespondentAddress2", Objects.toString(primary.getAddress().getAddress2(), ""));
        variables.put("primaryCorrespondentAddress3", Objects.toString(primary.getAddress().getAddress3(), ""));
        variables.put("primaryCorrespondentPostcode", Objects.toString(primary.getAddress().getPostcode(), ""));
        variables.put("primaryCorrespondentEmail", Objects.toString(primary.getEmail(), ""));
        variables.put("caseReference", caseDetails.getReference());
        variables.put("primaryCorrespondentRef", (primary.getReference()) != null ? primary.getReference() : "");
        variables.put("dateOfLetter", formatDate(caseDetails.getData().get("DateOfCorrespondence")));
        variables.put("constituentName", Objects.toString(constituent.getFullname(), ""));
        variables.put("constituentAddress1", (constituent.getAddress().getAddress1() != null ? constituent.getAddress().getAddress1() + ", " : ""));
        variables.put("constituentAddress2", (constituent.getAddress().getAddress2() != null ? constituent.getAddress().getAddress2() + ", " : ""));
        variables.put("constituentAddress3", (constituent.getAddress().getAddress3() != null ? constituent.getAddress().getAddress3() + ", " : ""));
        variables.put("constituentPostcode", (constituent.getAddress().getPostcode() != null ? constituent.getAddress().getPostcode() : ""));
        variables.put("poTeamLetterName", Objects.toString(team.getLetterName(), ""));
        return variables;
    }

    private byte[] generateTemplate(Map<String, String> variables, InputStream templateInputStream) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        VariablePrepare.prepare(wordMLPackage);

        documentPart.variableReplace(variables);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        wordMLPackage.save(outputStream);

        return outputStream.toByteArray();
    }
}
