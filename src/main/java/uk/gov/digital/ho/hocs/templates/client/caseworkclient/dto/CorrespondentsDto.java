package uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CorrespondentsDto {

    @JsonProperty("correspondents")
    private Set<CorrespondentDto> correspondents;
}
