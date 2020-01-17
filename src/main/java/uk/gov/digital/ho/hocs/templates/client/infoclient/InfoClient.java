package uk.gov.digital.ho.hocs.templates.client.infoclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.templates.application.RestHelper;
import uk.gov.digital.ho.hocs.templates.client.infoclient.dto.TeamDto;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.EVENT;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.INFO_CLIENT_GET_TEAM_FOR_TOPIC_STAGE_SUCCESS;

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

    public TeamDto getTeamForTopicAndStage(UUID caseUUID, UUID topicUUID, String stageType) {
        TeamDto response = restHelper.get(serviceBaseURL, String.format("/team/case/%s/topic/%s/stage/%s", caseUUID, topicUUID, stageType), TeamDto.class);
        log.info("Got Team teamUUID {} for Topic {} and Stage {}, event {}", response.getUuid(), topicUUID, stageType, value(EVENT, INFO_CLIENT_GET_TEAM_FOR_TOPIC_STAGE_SUCCESS));
        return response;
    }
}