package org.henry.bankingsystem.dto;

public record PasswordResetDto(
        Long otp,
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
