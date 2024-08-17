package org.henry.onlinebankingsystemp.dto;

import org.henry.onlinebankingsystemp.enums.AccountType;
import org.henry.onlinebankingsystemp.enums.CurrencyType;

import java.math.BigDecimal;

public record OnboardUserDto(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        AccountType accountType,
        CurrencyType currencyType,
        BigDecimal initialDeposit
) {
        public static void validate(OnboardUserDto dto) {
                if (dto.firstName == null || dto.firstName.isBlank()) {
                        throw new IllegalArgumentException("First name is required.");
                }
                if (dto.lastName == null || dto.lastName.isBlank()) {
                        throw new IllegalArgumentException("Last name is required.");
                }
                if (dto.email == null || !dto.email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        throw new IllegalArgumentException("A valid email is required.");
                }
                if (dto.password == null || dto.password.length() < 8) {
                        throw new IllegalArgumentException("Password must be at least 8 characters long.");
                }
                if (dto.phoneNumber == null || dto.phoneNumber.isBlank()) {
                        throw new IllegalArgumentException("Phone number is required.");
                }
                if (dto.initialDeposit == null || dto.initialDeposit.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Initial deposit must be greater than zero.");
                }
        }
}
