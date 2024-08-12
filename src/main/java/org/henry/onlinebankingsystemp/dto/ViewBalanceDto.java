package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ViewBalanceDto(
        String username,
        String accountNumber,
        BigDecimal balance,
        LocalDate lastUpdatedAt
) {}
