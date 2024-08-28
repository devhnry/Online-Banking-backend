package org.henry.onlinebankingsystemp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.henry.onlinebankingsystemp.enums.AccountType;
import org.henry.onlinebankingsystemp.enums.CurrencyType;
import org.intellij.lang.annotations.Pattern;
import java.math.BigDecimal;

public record OnboardUserDto(
        @NotNull(message = "First name is required")
        @NotBlank(message = "First name cannot be empty")
        String firstName,

        @NotNull(message = "Last name is required")
        @NotBlank(message = "Last name cannot be empty")
        String lastName,

        @NotBlank(message = "Email cannot be empty")
        @NotNull(message = "A valid email is required.")
        @Pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") @Email
        String email,

        @NotNull(message = "Password is required")
        @NotBlank(message = "Password cannot be empty")
        String password,

        @NotNull(message = "Hashed Pin is required")
        Long hashedPin,

        @NotNull(message = "Phone is required")
        @NotBlank(message = "Phone number cannot be empty")
        String phoneNumber,

        @NotNull(message = "Account Type cannot be null")
        String accountType,

        @NotNull(message = "Currency Type cannot be null")
        String currencyType,

        @NotNull(message = "Initial deposit cannot be null")
        BigDecimal initialDeposit
) {}