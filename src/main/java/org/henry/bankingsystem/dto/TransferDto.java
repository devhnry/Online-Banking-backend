package org.henry.bankingsystem.dto;

import java.math.BigDecimal;

public record TransferDto(
        String accountNumber,
        BigDecimal amount,
        String description,
        Long hashedPin
) {}
