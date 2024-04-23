package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class TransactionDataDTO {
    private String reference;
    private String status;
    private String amount;
    private String fee;
    private String currency;
    private String description;
    private PayerBankDTO payer_bank_account;
}
