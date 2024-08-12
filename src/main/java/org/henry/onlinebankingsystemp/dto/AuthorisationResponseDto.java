package org.henry.onlinebankingsystemp.dto;

public record AuthorisationResponseDto(
        String accessToken,
        String refreshToken,
        String issuedAt,
        String expirationTime
) {}
