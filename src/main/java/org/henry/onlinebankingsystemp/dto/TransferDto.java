package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;

public record TransferDto(
        String accountNumber,
        BigDecimal amount,
        String description,
        String hashedPin
) {}
