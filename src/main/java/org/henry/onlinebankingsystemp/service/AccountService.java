package org.henry.onlinebankingsystemp.service;

import jakarta.validation.Valid;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountService {
    DefaultApiResponse<CustomerDto> getDetails();
    DefaultApiResponse<ViewBalanceDto> checkBalance();
    DefaultApiResponse<ViewBalanceDto> depositMoney(DepositDto requestBody);
    DefaultApiResponse<BalanceDto> makeWithdrawal(WithdrawDto withdraw);
}


