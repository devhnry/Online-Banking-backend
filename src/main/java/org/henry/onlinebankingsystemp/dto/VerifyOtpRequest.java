package org.henry.onlinebankingsystemp.dto;

public record VerifyOtpRequest(
        String email,
        String password,
        String otpCode
){}
