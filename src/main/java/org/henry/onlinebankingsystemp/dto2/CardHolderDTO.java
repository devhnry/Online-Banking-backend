package org.henry.onlinebankingsystemp.dto2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardHolderDTO {
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String date_of_birth;
    private AddressDTO address;
    private CountryDTO country_identity;
    private IdentityDTO identity;
}
