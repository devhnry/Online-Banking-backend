package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionReqRes {
    private Long userId;
    private String username;
    private String email;
    private Double amount;
    private String description;
    private Double updated_balance;
    private Long account_number;
}
