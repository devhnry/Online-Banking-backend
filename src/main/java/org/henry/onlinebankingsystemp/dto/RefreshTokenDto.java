package org.henry.onlinebankingsystemp.dto;

public record RefreshTokenDto(
        String refreshToken
) {
    public static void validate(RefreshTokenDto dto) {
        if (dto.refreshToken == null || dto.refreshToken.length() < 8) {
            throw new IllegalArgumentException("Refresh Token not provided");
        }
    }
}
