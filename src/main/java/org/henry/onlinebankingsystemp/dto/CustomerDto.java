package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data @Builder
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {
    private String customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private AccountDto accountDetails;
}
