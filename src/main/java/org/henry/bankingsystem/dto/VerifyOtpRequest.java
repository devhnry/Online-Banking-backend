package org.henry.bankingsystem.dto;

public record VerifyOtpRequest(
        String email,
        String password,
        String otpCode
){}
