package uk.gov.digital.ho.hocs.templates.client.caseworkclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.templates.application.RestHelper;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CaseDataDto;
import uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto.CorrespondentsDto;
import uk.gov.digital.ho.hocs.templates.domain.exception.ApplicationExceptions;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;

import static uk.gov.digital.ho.hocs.templates.application.LogEvent.*;

@Slf4j
@Component
public class CaseworkClient {

    private final RestHelper restHelper;
    private final String serviceBaseURL;

    @Autowired
    public CaseworkClient(RestHelper restHelper,
                          @Value("${hocs.case-service}") String caseService) {
        this.restHelper = restHelper;
        this.serviceBaseURL = caseService;
    }

    public CaseDataDto getCase(UUID caseUUID) {
        CaseDataDto response = restHelper.get(serviceBaseURL, String.format("/case/%s", caseUUID), CaseDataDto.class);
        log.info("Got Case details for case: {}", caseUUID, value(EVENT, CASE_CLIENT_GET_CASE_DETAILS_SUCCESS));
        return response;
    }

    public CorrespondentsDto getCorrespondents(UUID caseUUID) {
        CorrespondentsDto correspondents = restHelper.get(serviceBaseURL, String.format("/case/%s/correspondent", caseUUID), CorrespondentsDto.class);
        log.info("Got Correspondent for case: {}", caseUUID, value(EVENT, CASE_CLIENT_GET_CORRESPONDENT_DETAILS_SUCCESS));
        if(correspondents.getCorrespondents().isEmpty() || correspondents.getCorrespondents().size() == 0){
            throw new ApplicationExceptions.EntityNotFoundException(String.format("Correspondent: %s, not found!", caseUUID), CORRESPONDENT_NOT_FOUND);
        } else {
            return correspondents;
        }
    }
}
