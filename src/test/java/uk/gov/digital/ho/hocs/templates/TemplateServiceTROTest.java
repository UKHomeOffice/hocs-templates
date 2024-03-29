package uk.gov.digital.ho.hocs.templates;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.CaseworkClient;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.AddressDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CaseDataDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentsDto;
import uk.gov.digital.ho.hocs.templates.client.documentclient.DocumentClient;
import uk.gov.digital.ho.hocs.templates.client.infoclient.InfoClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTROTest {
    private static final String CASE_TYPE = "TRO";

    private final UUID CASE_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID TEMPLATE_DOCS_UUID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private final UUID PRIMARY_CORRESPONDENT_UUID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID TOPIC_UUID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private final Map<String, String> DATA = new HashMap<>() {{
        put("DateOfCorrespondence", "2019-03-20");
    }};
    private final CaseDataDto CASE_DETAILS = new CaseDataDto(CASE_UUID, CASE_TYPE, "caseReference", DATA, TOPIC_UUID, PRIMARY_CORRESPONDENT_UUID);

    private TemplateService templateService;

    @Mock
    CaseworkClient caseworkClient;
    @Mock
    InfoClient infoClient;
    @Mock
    DocumentClient documentClient;

    @BeforeEach
    public void setUp() {
        this.templateService = new TemplateService(caseworkClient, infoClient, documentClient);
    }

    @Test
    public void shouldReturnPopulatedTemplate() {
        when(caseworkClient.getCase(CASE_UUID)).thenReturn(CASE_DETAILS);
        when(documentClient.getTemplate(TEMPLATE_DOCS_UUID)).thenReturn(getDocumentByteArray());
        when(caseworkClient.getCorrespondents(CASE_UUID)).thenReturn(getCorrespondents());
        when(infoClient.getCaseTypeStages("TRO")).thenReturn(Collections.emptyList());

        TemplateResult result = templateService.buildTemplate(CASE_UUID, TEMPLATE_DOCS_UUID);

        assertThat(result).isNotNull();
        assertThat(result.getFilename()).isEqualTo("caseReference");

        verify(caseworkClient).getCase(CASE_UUID);
        verify(documentClient).getTemplate(TEMPLATE_DOCS_UUID);
        verify(caseworkClient).getCorrespondents(CASE_UUID);
        verify(infoClient, times(1)).getCaseTypeStages(eq("TRO"));
        verify(infoClient, never()).getTeamForTopicAndStage(CASE_UUID, TOPIC_UUID, "DCU_TRO_PRIVATE_OFFICE");

        verifyNoMoreInteractions(caseworkClient);
        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(documentClient);
    }

    private ByteArrayResource getDocumentByteArray() {
        byte[] bytes = null;
        try {
            InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream("testdata/tro.docx");
            bytes = IOUtils.toByteArray(templateInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayResource(bytes);
    }

    private CorrespondentsDto getCorrespondents() {
        AddressDto address = new AddressDto("S1 1CJ", "1 Somewhere Street", "Somewhere", "", "");
        CorrespondentDto primaryCorrespondent = new CorrespondentDto(PRIMARY_CORRESPONDENT_UUID, LocalDateTime.now(), "MEMBER", CASE_UUID, "Bob Smith MP", address, "", "", "ref1");
        CorrespondentDto constituent = new CorrespondentDto(UUID.fromString("66666666-6666-6666-6666-666666666666"), LocalDateTime.now(), "CONSTITUENT", CASE_UUID, "Bob", address, "", "", "");
        return new CorrespondentsDto(Stream.of(primaryCorrespondent, constituent).collect(Collectors.toCollection(HashSet::new)));
    }
}
