package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.henry.onlinebankingsystemp.dto.enums.AccountType;
import org.henry.onlinebankingsystemp.entity.*;

import java.util.List;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultResponse {
    private int statusCode;
    private String message;
    private Object data;
}

