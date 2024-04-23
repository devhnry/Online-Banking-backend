package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.henry.onlinebankingsystemp.dto.enums.TransactionType;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithdrawDTO {
    private String userId;
    private String accountId;
    private BigDecimal amount;
    private BigDecimal balance;
}