package uk.gov.digital.ho.hocs.templates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.utils.IOUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.CaseworkClient;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.AddressDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentsDto;
import uk.gov.digital.ho.hocs.templates.client.documentclient.DocumentClient;
import uk.gov.digital.ho.hocs.templates.client.documentclient.dto.TemplateDocsDataDto;
import uk.gov.digital.ho.hocs.templates.client.documentclient.dto.TemplatesDocsDataDto;
import uk.gov.digital.ho.hocs.templates.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.templates.client.infoclient.dto.TeamDto;
import uk.gov.digital.ho.hocs.templates.client.infoclient.dto.TemplateInfoDataDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.MockRestServiceServer.bindTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TemplateServiceIntTest {

    private TemplateService templateService;
    private MockRestServiceServer mockService;

    TestRestTemplate testRestTemplate = new TestRestTemplate();

    @LocalServerPort
    int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CaseworkClient caseworkClient;
    @Autowired
    InfoClient infoClient;
    @Autowired
    DocumentClient documentClient;

    private static final String MIN = "MIN";
    private final UUID CASE_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID TEMPLATE_INFO_UUID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID TEMPLATE_DOCS_UUID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private final UUID PRIMARY_CORRESPONDENT_UUID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final TemplateInfoDataDto MIN_TEMPLATE_INFO_DATA = new TemplateInfoDataDto("Min Template", TEMPLATE_INFO_UUID, MIN);
    private final TemplateDocsDataDto MIN_TEMPLATE_DOCS_DATA = new TemplateDocsDataDto(TEMPLATE_DOCS_UUID, TEMPLATE_INFO_UUID, "TEMPLATE", "MIN template", "UPLOADED", LocalDateTime.now(), LocalDateTime.now(), false);
    private final TemplatesDocsDataDto MIN_TEMPLATES_DOCS_DATA = new TemplatesDocsDataDto(new HashSet<>(Arrays.asList(MIN_TEMPLATE_DOCS_DATA)));


    @Before
    public void setUp() throws IOException {
        this.templateService = new TemplateService(caseworkClient, infoClient, documentClient);
        mockService = buildMockService(restTemplate);

        mockService
                .expect(requestTo("http://localhost:8082/case/11111111-1111-1111-1111-111111111111/correspondent"))
                .andExpect(method(GET))
                .andRespond(withSuccess(mapper.writeValueAsString(getCorrespondents()), MediaType.APPLICATION_JSON));
        mockService
                .expect(requestTo("http://localhost:8082/case/11111111-1111-1111-1111-111111111111"))
                .andExpect(method(GET))
                .andRespond(withSuccess(getCaseDetailsAsJsonString(), MediaType.APPLICATION_JSON));
        mockService
                .expect(requestTo("http://localhost:8085/caseType/MIN/template"))
                .andExpect(method(GET))
                .andRespond(withSuccess(mapper.writeValueAsString(MIN_TEMPLATE_INFO_DATA), MediaType.APPLICATION_JSON));
        mockService
                .expect(requestTo("http://localhost:8083/document/case/22222222-2222-2222-2222-222222222222/TEMPLATE"))
                .andExpect(method(GET))
                .andRespond(withSuccess(mapper.writeValueAsString(MIN_TEMPLATES_DOCS_DATA), MediaType.APPLICATION_JSON));
        mockService
                .expect(requestTo("http://localhost:8083/document/33333333-3333-3333-3333-333333333333/file"))
                .andExpect(method(GET))
                .andRespond(withSuccess(getDocumentByteArray(), MediaType.APPLICATION_OCTET_STREAM));
        mockService
                .expect(requestTo("http://localhost:8085/team/case/11111111-1111-1111-1111-111111111111/topic/55555555-5555-5555-5555-555555555555/stage/DCU_MIN_PRIVATE_OFFICE"))
                .andExpect(method(GET))
                .andRespond(withSuccess(mapper.writeValueAsString(getTeam()), MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnPopulatedMINTemplate() throws Exception {

        ResponseEntity<byte[]> result = testRestTemplate.exchange(
                getBasePath() + "/template/" + CASE_UUID, GET, new HttpEntity(createValidAuthHeaders()), byte[].class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        InputStream templateInputStream = new ByteArrayInputStream(result.getBody());

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        List<String> contentList = documentPart.getContent().stream().map(b -> b.toString()).collect(Collectors.toList());

        assertThat(contentList).contains("Bob Smith MP");
        assertThat(contentList).contains("1 Somewhere Street");
        assertThat(contentList).contains("Somewhere");
        assertThat(contentList).contains("S1 1DJ");
        assertThat(contentList).contains("Reference: MIN/000001/19");
        assertThat(contentList).contains("ref1");
        assertThat(contentList).contains("Thank you for your letter of 20 March 2019 on behalf of Jon of 2 Elsewhere Road, AnyWhere, North, N1 2XR about");
        assertThat(contentList).contains("Murdock of The A Team");
        assertThat(contentList).doesNotContain("${primaryCorrespondentName}");
        assertThat(contentList).doesNotContain("${primaryCorrespondentAddress1}");
        assertThat(contentList).doesNotContain("${primaryCorrespondentAddress2}");
        assertThat(contentList).doesNotContain("${primaryCorrespondentAddress3}");
        assertThat(contentList).doesNotContain("${primaryCorrespondentPostcode}");
        assertThat(contentList).doesNotContain("Reference: ${caseReference}");
        assertThat(contentList).doesNotContain("${primaryCorrespondentRef}");
        assertThat(contentList).doesNotContain("Thank you for your letter of ${dateOfLetter} on behalf of ${constituentName} of ${constituentAddress1}${constituentAddress2}${constituentAddress3}${constituentPostcode} about");
        assertThat(contentList).doesNotContain("${poTeamLetterName}");

    }


    private MockRestServiceServer buildMockService(RestTemplate restTemplate) {
        MockRestServiceServer.MockRestServiceServerBuilder infoBuilder = bindTo(restTemplate);
        infoBuilder.ignoreExpectOrder(true);
        return infoBuilder.build();
    }

    private String getBasePath() {
        return "http://localhost:" + port;
    }

    private HttpHeaders createValidAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Auth-Groups", "/RERERCIiIiIiIiIiIiIiIg");
        headers.add("X-Auth-Userid", "a.person@digital.homeoffice.gov.uk");
        headers.add("X-Correlation-Id", "1");
        return headers;
    }


    private ByteArrayResource getDocumentByteArray() throws IOException {
        byte[] bytes = null;
        try {
            InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream("testdata/min.docx");
            bytes = IOUtils.toByteArray(templateInputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayResource(bytes);
    }

    private CorrespondentsDto getCorrespondents() {
        AddressDto address = new AddressDto("S1 1DJ", "1 Somewhere Street", "Somewhere", "", "");
        AddressDto address1 = new AddressDto("N1 2XR", "2 Elsewhere Road", "AnyWhere", "North", "");
        CorrespondentDto primaryCorrespondent = new CorrespondentDto(PRIMARY_CORRESPONDENT_UUID, LocalDateTime.now(), "MEMBER", CASE_UUID, "Bob Smith MP", address, "", "", "ref1");
        CorrespondentDto constituent = new CorrespondentDto(UUID.fromString("66666666-6666-6666-6666-666666666666"), LocalDateTime.now(), "CONSTITUENT", CASE_UUID, "Jon", address1, "", "", "");
        return new CorrespondentsDto(Stream.of(primaryCorrespondent, constituent).collect(Collectors.toCollection(HashSet::new)));

    }


    private TeamDto getTeam() {
        return new TeamDto("The A Team", UUID.fromString("77777777-7777-7777-7777-77777777777"), Boolean.TRUE, "Murdock of The A Team");

    }

    private String getCaseDetailsAsJsonString() throws JsonProcessingException {
        return "{ \"data\" : {\"DateOfCorrespondence\":\"2019-03-20\"}," +
                "\"uuid\" : \"11111111-1111-1111-1111-111111111111\"," +
                "\"created\" : \"2019-04-30T15:56:24.241626\"," +
                "\"type\" : \"MIN\"," +
                "\"reference\" : \"MIN/000001/19\"," +
                "\"primaryTopic\" : \"55555555-5555-5555-5555-555555555555\"," +
                "\"primaryCorrespondent\" : \"44444444-4444-4444-4444-444444444444\" " +
                "}";

    }
}