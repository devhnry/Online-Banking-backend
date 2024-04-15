package org.henry.onlinebankingsystemp.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.AccountType;
import org.henry.onlinebankingsystemp.otp.OTP;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {
    private String firstName;
    private String lastName;
    private String email;
    private Long phoneNumber;
    private AccountType accountType;
    private Account accountDetails;
    private String username;
    private Long otpCode;
}
