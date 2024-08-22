package org.henry.onlinebankingsystemp.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.henry.onlinebankingsystemp.enums.AccountType;
import org.henry.onlinebankingsystemp.enums.CurrencyType;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {
    private Long accountId;
    private String accountNumber;
    private String accountHolderName;
    private AccountType accountType;
    private CurrencyType currencyType;
    private BigDecimal balance;
}
