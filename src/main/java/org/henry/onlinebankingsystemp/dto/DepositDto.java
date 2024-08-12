package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;

public record DepositDto(
        BigDecimal amountToDeposit
) {}
