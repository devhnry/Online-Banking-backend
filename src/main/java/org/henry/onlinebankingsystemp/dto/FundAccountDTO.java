package org.henry.onlinebankingsystemp.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountDTO {
    private String account_number;
    private Double amount;
    private String currency;
}
