package uk.gov.digital.ho.hocs.templates;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TemplateResourceTest {

    private final UUID caseUUID = UUID.randomUUID();
    private final UUID templateUUID = UUID.randomUUID();
    private final TemplateResult result = new TemplateResult("file", caseUUID.toString().getBytes());
    @Mock
    private TemplateService templateService;
    private TemplateResource templateResource;

    @Before
    public void setUp() {
        templateResource = new TemplateResource(templateService);
    }

    @Test
    public void shouldCreateTemplateFromCaseUUID() {

        when(templateService.buildTemplate(caseUUID, templateUUID)).thenReturn(result);

        ResponseEntity<byte[]> response = templateResource.populateTemplate(caseUUID, templateUUID);

        verify(templateService).buildTemplate(caseUUID, templateUUID);

        verifyNoMoreInteractions(templateService);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}