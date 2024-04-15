package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.henry.onlinebankingsystemp.entity.TransactionType;

import java.util.Date;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {
    private TransactionType transactionType;
    private Double amount;
    private Date dateTime;
    private String description;
    private Long targetAccountNumber;
}
