package org.henry.bankingsystem.dto;

import java.math.BigDecimal;

public record TransactionSummaryDto(
        BigDecimal transferAmount,
        BigDecimal charges,
        BigDecimal totalAmount
) {}
