package org.henry.bankingsystem.dto;

public record PasswordChangeDto(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
