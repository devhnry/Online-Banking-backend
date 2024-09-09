package org.henry.bankingsystem.dto;

import java.math.BigDecimal;

public record TransactionLimitDto(
        Long otpCode,
        BigDecimal amount,
        String password
) {}
