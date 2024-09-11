package org.henry.bankingsystem.dto;

import java.math.BigDecimal;

public record ViewBalanceDto(
        String email,
        String accountNumber,
        BigDecimal balance,
        String lastUpdatedAt
) {}
