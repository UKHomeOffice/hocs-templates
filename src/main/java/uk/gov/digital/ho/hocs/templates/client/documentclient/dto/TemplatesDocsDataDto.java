package uk.gov.digital.ho.hocs.templates.client.documentclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TemplatesDocsDataDto {

        @JsonProperty("documents")
        private Set<TemplateDocsDataDto> templatesData;
}
