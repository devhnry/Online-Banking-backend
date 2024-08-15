package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.enums.CurrencyType;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessfulOnboardDto {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private AccountDto account;
}
