package org.henry.bankingsystem.dto;

public record UpdateInfoDTO(
        String email,
        String phoneNumber,
        Long otpCode
) {}
