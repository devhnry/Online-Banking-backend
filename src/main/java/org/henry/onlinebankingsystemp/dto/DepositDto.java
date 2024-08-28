package org.henry.onlinebankingsystemp.dto;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public record DepositDto(
        @NotNull("Amount to Deposit is required")
        BigDecimal amountToDeposit
) {}
