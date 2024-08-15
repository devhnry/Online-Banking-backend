package org.henry.onlinebankingsystemp.dto;

import java.time.Instant;

public record AuthorisationResponseDto(
        String accessToken,
        String refreshToken,
        Instant issuedAt,
        String expirationTime
) {}
