package uk.gov.digital.ho.hocs.templates.client.infoclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.digital.ho.hocs.templates.application.RestHelper;
import uk.gov.digital.ho.hocs.templates.client.infoclient.dto.TeamDto;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class InfoClientTest {

    @Mock
    RestHelper restHelper;

    private final String serviceBaseURL = "http://localhost:8085";
    private final UUID uuid = UUID.randomUUID();
    private final String MIN = "MIN";
    private final String STAGE_TYPE = "DCU_MIN_PRIVATE_OFFICE";

    private InfoClient infoClient;

    @Before
    public void setUp() {
        this.infoClient = new InfoClient(restHelper, serviceBaseURL);
    }

    @Test
    public void shouldGetTeamData() {
        TeamDto teamDto = new TeamDto("min", uuid, Boolean.TRUE, MIN);

        when(restHelper.get(eq(serviceBaseURL), eq(String.format("/team/case/%s/topic/%s/stage/%s", uuid, uuid, STAGE_TYPE)), eq(TeamDto.class))).thenReturn(teamDto);

        infoClient.getTeamForTopicAndStage(uuid, uuid, STAGE_TYPE);

        verify(restHelper, times(1)).get(eq(serviceBaseURL), eq(String.format("/team/case/%s/topic/%s/stage/%s", uuid, uuid, STAGE_TYPE)), eq(TeamDto.class));
        verifyNoMoreInteractions(restHelper);
    }


    @Test(expected = HttpClientErrorException.NotFound.class)
    public void shouldThrowNotFoundExceptionWhenGetTemplate() {
        doThrow(HttpClientErrorException.NotFound.class).when(restHelper).get(eq(serviceBaseURL), eq(String.format("/team/case/%s/topic/%s/stage/%s", uuid, uuid, STAGE_TYPE)), eq(TeamDto.class));
        infoClient.getTeamForTopicAndStage(uuid, uuid, STAGE_TYPE);

        verify(restHelper, Mockito.times(1)).get(eq(serviceBaseURL), eq(String.format("/team/case/%s/topic/%s/stage/%s", uuid, uuid, STAGE_TYPE)), eq(TeamDto.class));
        verifyNoMoreInteractions(restHelper);
    }
}