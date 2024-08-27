package org.henry.onlinebankingsystemp.service;

import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Customer;

public interface AccountService {
    DefaultApiResponse<CustomerDto> getDetails();
    DefaultApiResponse<ViewBalanceDto> checkBalance();
    DefaultApiResponse<ViewBalanceDto> depositMoney(DepositDto requestBody);
}


