package uk.gov.digital.ho.hocs.templates.client.caseworkclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CorrespondentDto {

    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("created")
    private LocalDateTime created;

    @JsonProperty("type")
    private String type;

    @JsonProperty("caseUUID")
    private UUID caseUUID;

    @JsonProperty("fullname")
    private String fullname;

    @JsonProperty("address")
    private AddressDto address;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("reference")
    private String reference;

    public CorrespondentDto(AddressDto address) {
        this.address = address;
    }
}
