package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationDTO {
    private String page;
    private String total;
    private String pageCount;
    private String totalPages;
}
