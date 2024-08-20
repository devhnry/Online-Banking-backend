package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ViewBalanceDto(
        String username,
        String accountNumber,
        BigDecimal balance,
        String lastUpdatedAt
) {}
