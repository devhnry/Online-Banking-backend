package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.henry.onlinebankingsystemp.dto.enums.AccountType;
import org.henry.onlinebankingsystemp.entity.Address;
import org.henry.onlinebankingsystemp.entity.Country;
import org.henry.onlinebankingsystemp.entity.Identity;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class SignUpDTO {
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Address address;
    private Country country;
    private Identity identity;
    private AccountType accountType;
}
