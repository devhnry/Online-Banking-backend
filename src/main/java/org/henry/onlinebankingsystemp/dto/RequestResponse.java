package org.henry.onlinebankingsystemp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.henry.onlinebankingsystemp.entity.*;

import java.util.List;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestResponse {
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String full_name;
    private String first_name;
    private String last_name;
    private String email;
    private String role;
    private String password;
    private AccountType account_type;
    private Long phone_number;
    private Users users;
    private Admin admin;
    private List<Transaction> transactions;
    private Account account;
    private Double transactionLimit;
}

