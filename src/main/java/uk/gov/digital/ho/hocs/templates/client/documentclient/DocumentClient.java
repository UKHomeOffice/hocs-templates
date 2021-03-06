package uk.gov.digital.ho.hocs.templates.client.documentclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.templates.application.RestHelper;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.DOCS_CLIENT_GET_TEMPLATE_SUCCESS;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.EVENT;


@Slf4j
@Component
public class DocumentClient {

    private final RestHelper restHelper;
    private final String serviceBaseURL;

    @Autowired
    public DocumentClient(RestHelper restHelper,
                          @Value("${hocs.document-service}") String documentService) {
        this.restHelper = restHelper;
        this.serviceBaseURL = documentService;
    }

    public ByteArrayResource getTemplate(UUID templateUUID) {
        ByteArrayResource template = restHelper.get(serviceBaseURL, String.format("/document/%s/file", templateUUID), ByteArrayResource.class);
        log.info("Got Template template for template uuid {}, event {}", templateUUID, value(EVENT, DOCS_CLIENT_GET_TEMPLATE_SUCCESS));
        return template;
    }
}
