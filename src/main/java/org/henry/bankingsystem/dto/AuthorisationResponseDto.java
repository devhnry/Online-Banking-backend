package org.henry.bankingsystem.dto;

public record AuthorisationResponseDto(
        String accessToken,
        String refreshToken,
        String issuedAt,
        String expirationTime
) {}
