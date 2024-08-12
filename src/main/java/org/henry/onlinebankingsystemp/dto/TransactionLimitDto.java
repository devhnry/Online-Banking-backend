package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;

public record TransactionLimitDto(
        Long otpCode,
        BigDecimal amount,
        String password
) {}
