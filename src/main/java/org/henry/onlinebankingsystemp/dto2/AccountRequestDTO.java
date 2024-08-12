package org.henry.onlinebankingsystemp.dto2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountRequestDTO {
    private String account_name;
    private String account_reference;
    private Boolean permanent;
    private String bank_code;
    private UserDTO customer;
    private KYC kyc;
}
