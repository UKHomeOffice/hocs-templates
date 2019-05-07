package uk.gov.digital.ho.hocs.templates.client.infoclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.templates.application.RestHelper;
import uk.gov.digital.ho.hocs.templates.client.infoclient.dto.TeamDto;
import uk.gov.digital.ho.hocs.templates.client.infoclient.dto.TemplateInfoDataDto;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.*;

@Slf4j
@Component
public class InfoClient {

    private final RestHelper restHelper;
    private final String serviceBaseURL;

    @Autowired
    public InfoClient(RestHelper restHelper,
                      @Value("${hocs.info-service}") String infoService) {
        this.restHelper = restHelper;
        this.serviceBaseURL = infoService;
    }

    public TemplateInfoDataDto getTemplateUUID(String caseType) {
        TemplateInfoDataDto response = restHelper.get(serviceBaseURL, String.format("/caseType/%s/template", caseType), TemplateInfoDataDto.class);
        log.info("Got Template templateUUID {} for case tyoe {}", response, caseType, value(EVENT, INFO_CLIENT_GET_TEMPLATE_UUID_SUCCESS));
        return response;
    }

    public TeamDto getTeamForTopicAndStage(UUID caseUUID, UUID topicUUID, String stageType) {
        TeamDto response = restHelper.get(serviceBaseURL, String.format("/team/case/%s/topic/%s/stage/%s", caseUUID, topicUUID, stageType), TeamDto.class);
        log.info("Got Team teamUUID {} for Topic {} and Stage {}", response.getUuid(), topicUUID, stageType, value(EVENT, INFO_CLIENT_GET_TEAM_FOR_TOPIC_STAGE_SUCCESS));
        return response;
    }
}