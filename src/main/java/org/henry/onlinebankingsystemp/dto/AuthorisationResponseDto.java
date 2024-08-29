package org.henry.onlinebankingsystemp.dto;

import java.time.Instant;

public record AuthorisationResponseDto(
        String accessToken,
        String refreshToken,
        String issuedAt,
        String expirationTime
) {}
