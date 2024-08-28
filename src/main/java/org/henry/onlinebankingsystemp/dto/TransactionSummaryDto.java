package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;

public record TransactionSummaryDto(
        BigDecimal transferAmount,
        BigDecimal charges,
        BigDecimal totalAmount
) {}
