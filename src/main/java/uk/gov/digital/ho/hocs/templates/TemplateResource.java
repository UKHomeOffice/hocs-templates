package uk.gov.digital.ho.hocs.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

@RestController
public class TemplateResource {


    private final TemplateService templateService;

    @Autowired
    public TemplateResource(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping(value = "case/{caseUUID}/template/{templateUUID}", produces = APPLICATION_OCTET_STREAM)
    public ResponseEntity<byte[]> populateTemplate(@PathVariable UUID caseUUID, @PathVariable UUID templateUUID) {
        TemplateResult result = templateService.buildTemplate(caseUUID, templateUUID);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.getFilename() + ".docx\"")
                .contentType(MediaType.asMediaType(MediaType.APPLICATION_OCTET_STREAM))
                .contentLength(result.getTemplateData().length)
                .body(result.getTemplateData());
    }
}
