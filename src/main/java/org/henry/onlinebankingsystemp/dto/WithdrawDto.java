package org.henry.onlinebankingsystemp.dto;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public record WithdrawDto(
        @NotNull("Amount to Withdraw is required")
        BigDecimal amountToWithdraw,
        @NotNull("Pin is required")
        Long hashedPin
) {}
