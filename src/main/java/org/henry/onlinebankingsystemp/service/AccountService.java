package org.henry.onlinebankingsystemp.service;

import org.henry.onlinebankingsystemp.dto.CustomerDto;
import org.henry.onlinebankingsystemp.dto.DefaultApiResponse;
import org.henry.onlinebankingsystemp.dto.ViewBalanceDto;
import org.henry.onlinebankingsystemp.entity.Customer;

public interface AccountService {
    DefaultApiResponse<CustomerDto> getDetails();
    DefaultApiResponse<ViewBalanceDto> checkBalance();
}


