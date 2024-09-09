package org.henry.bankingsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BalanceDto{
    private String email;
    private String requestType;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String description;
    private String lastUpdatedAt;
}

