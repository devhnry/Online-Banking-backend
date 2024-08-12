package org.henry.onlinebankingsystemp.dto;

public record PasswordResetDto(
        Long otp,
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
