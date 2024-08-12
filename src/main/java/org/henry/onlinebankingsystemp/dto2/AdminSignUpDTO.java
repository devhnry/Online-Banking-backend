package org.henry.onlinebankingsystemp.dto2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class AdminSignUpDTO {
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}
