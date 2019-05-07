package uk.gov.digital.ho.hocs.templates.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateRequestDto {

    @JsonProperty("template_name")
    private String templateName;
}
