package uk.gov.digital.ho.hocs.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

@RestController
public class TemplateResource {


    private final TemplateService templateService;

    @Autowired
    public TemplateResource(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping(value = "/template/{caseUUID}", produces = APPLICATION_OCTET_STREAM)
    public ResponseEntity<byte[]> populateTemplate(@PathVariable UUID caseUUID) {

        byte[] result = templateService.buildTemplate(caseUUID);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"template.docx\"")
                .contentType(MediaType.asMediaType(MediaType.APPLICATION_OCTET_STREAM))
                .contentLength(result.length)
                .body(result);

    }
}