package org.henry.onlinebankingsystemp.dto;

import org.henry.onlinebankingsystemp.enums.AccountType;
import org.henry.onlinebankingsystemp.enums.CurrencyType;

import java.math.BigDecimal;

public record OnboardUserDto(
    String firstName,
    String lastName,
    String email,
    String password,
    String phoneNumber,
    AccountType accountType,
    CurrencyType currencyType,
    BigDecimal initialDeposit
) {}
