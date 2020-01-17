package uk.gov.digital.ho.hocs.templates.client.documentclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.digital.ho.hocs.templates.application.RestHelper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DocumentClientTest {

    @Mock
    RestHelper restHelper;

    private final String serviceBaseURL = "http://localhost:8087";
    private final UUID uuid = UUID.randomUUID();

    private DocumentClient documentClient;

    @Before
    public void setUp() {
        this.documentClient = new DocumentClient(restHelper, serviceBaseURL);
    }

    @Test
    public void shouldGetTemplate() {
        ByteArrayResource resource = new ByteArrayResource("document".getBytes());

        when(restHelper.get(eq(serviceBaseURL), eq(String.format("/document/%s/file", uuid)), eq(ByteArrayResource.class))).thenReturn(resource);

        documentClient.getTemplate(uuid);

        verify(restHelper, times(1)).get(eq(serviceBaseURL), eq(String.format("/document/%s/file", uuid)), eq(ByteArrayResource.class));
        verifyNoMoreInteractions(restHelper);
    }

    @Test(expected = HttpClientErrorException.NotFound.class)
    public void shouldThrowNotFoundExceptionWhenGetTemplate() {
        doThrow(HttpClientErrorException.NotFound.class).when(restHelper).get(eq(serviceBaseURL), eq(String.format("/document/%s/file", uuid)), eq(ByteArrayResource.class));
        documentClient.getTemplate(uuid);

        verify(restHelper, times(1)).get(eq(serviceBaseURL), eq(String.format("/document/%s/file", uuid)), eq(ByteArrayResource.class));
        verifyNoMoreInteractions(restHelper);
    }
}