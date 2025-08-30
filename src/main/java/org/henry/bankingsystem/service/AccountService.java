package org.henry.bankingsystem.service;

import org.henry.bankingsystem.dto.*;

public interface AccountService {
    DefaultApiResponse<CustomerDto> getDetails();
    DefaultApiResponse<ViewBalanceDto> checkBalance();
    DefaultApiResponse<ViewBalanceDto> depositMoney(DepositDto requestBody);
    DefaultApiResponse<BalanceDto> makeWithdrawal(WithdrawDto requestBody);
    DefaultApiResponse<BalanceDto> makeTransfer(TransferDto requestBody);

    DefaultApiResponse<TransactionSummaryDto> displayTransferSummary(TransferDto requestBody);

    String getAccountHolderName(String accountNumber);
    
    DefaultApiResponse<?> getBankStatement();
    DefaultApiResponse<?> changePassword(PasswordChangeDto passwordChange);
    DefaultApiResponse<?> updateProfile(UpdateInfoDTO updateInfo);
    DefaultApiResponse<?> updateTransactionLimit(TransactionLimitDto transactionLimitDto);
}


