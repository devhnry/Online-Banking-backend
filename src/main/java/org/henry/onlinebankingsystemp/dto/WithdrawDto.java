package org.henry.onlinebankingsystemp.dto;

import java.math.BigDecimal;

public record WithdrawDto(
        BigDecimal amountToWithdraw,
        String hashedPin
) {}
