package org.henry.onlinebankingsystemp.service;

import jakarta.validation.Valid;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.entity.Transaction;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountService {
    DefaultApiResponse<CustomerDto> getDetails();
    DefaultApiResponse<ViewBalanceDto> checkBalance();
    DefaultApiResponse<ViewBalanceDto> depositMoney(DepositDto requestBody);
    DefaultApiResponse<BalanceDto> makeWithdrawal(WithdrawDto requestBody);
    DefaultApiResponse<BalanceDto> makeTransfer(TransferDto requestBody);

    DefaultApiResponse<TransactionSummaryDto> displayTransferSummary(TransferDto requestBody);

    String getAccountHolderName(String accountNumber);
}


