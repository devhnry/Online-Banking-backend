package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BalanceDto (
        String username,
        String requestType,
        String accountNumber,
        BigDecimal amount,
        BigDecimal balance,
        LocalDate date
) {}

