package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class LoginResponseDTO {
    private Integer statusCode;
    private String message;
    private String token;
    private String refreshToken;
    private String email;
    private String expirationTime;
}
