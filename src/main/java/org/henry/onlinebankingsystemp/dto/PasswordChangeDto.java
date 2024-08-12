package org.henry.onlinebankingsystemp.dto;

public record PasswordChangeDto(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
