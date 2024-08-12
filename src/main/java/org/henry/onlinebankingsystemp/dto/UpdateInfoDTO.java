package org.henry.onlinebankingsystemp.dto;

public record UpdateInfoDTO(
        String email,
        String phoneNumber,
        Long otpCode
) {}
