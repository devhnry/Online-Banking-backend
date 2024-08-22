package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OneTimePasswordDto {
    private String otpCode;
    private Boolean expired;
    private Instant generatedTime;
    private Instant expirationTime;
    private String expirationDuration;
    private CustomerDto customer;
}
