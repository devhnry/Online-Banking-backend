package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionKoraDTO {
    private String total_amount_received;
    private String account_number;
    private String currency;
    private List<TransactionDataDTO> transactions;
    private PaginationDTO pagination;
}
