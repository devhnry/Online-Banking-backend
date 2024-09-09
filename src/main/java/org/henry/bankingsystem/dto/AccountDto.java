package org.henry.bankingsystem.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.henry.bankingsystem.enums.AccountType;
import org.henry.bankingsystem.enums.CurrencyType;

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
