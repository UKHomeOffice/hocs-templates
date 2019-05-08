package uk.gov.digital.ho.hocs.templates.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserInformationDto {

    private String member;
    private String member_address1;
    private String member_address2;
    private String member_address3;
    private String member_postcode;
    private String reference;
    private String mp_ref;
    private String date_of_letter;
    private String third_party_correspondent_name;
    private String correspondent_address_line1;
    private String correspondent_address_line2;
    private String correspondent_address_line3;
    private String correspondent_postcode;
    private String po_team_name;

}
