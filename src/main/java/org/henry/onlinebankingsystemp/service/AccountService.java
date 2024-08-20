package org.henry.onlinebankingsystemp.service;

import org.henry.onlinebankingsystemp.dto.DefaultApiResponse;
import org.henry.onlinebankingsystemp.dto.ViewBalanceDto;

public interface AccountService {
    DefaultApiResponse<ViewBalanceDto> checkBalance();
}


