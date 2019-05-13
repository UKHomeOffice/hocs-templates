package uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CaseDataDto {

    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("type")
    private String type;

    @JsonProperty("reference")
    private String reference;

    @JsonRawValue
    private Map<String, String> data;

    @JsonProperty("primaryTopic")
    private UUID primaryTopic;

    @JsonProperty("primaryCorrespondent")
    private UUID primaryCorrespondent;


}
