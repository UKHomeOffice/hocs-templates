package uk.gov.digital.ho.hocs.templates;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TemplateResult {

    private String filename;
    private byte[] templateData;
}
