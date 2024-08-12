package org.henry.onlinebankingsystemp.dto2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayerBankDTO {
    private String account_number;
    private String account_name;
    private String bank_name;
}
