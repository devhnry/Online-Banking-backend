package org.henry.bankingsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultApiResponse<T> {
    private int statusCode;
    private String statusMessage;
    private T data;
}

