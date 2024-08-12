package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class AccountController {

//    private final CustomerService customerService;
//    private final AccountService accountService;

    /**
     * Endpoint to view the current account balance.
     * @return the balance of the user's account.
     */
    @GetMapping("/view-balance")
    public ResponseEntity<DefaultApiResponse<ViewBalanceDto>> getBalance(){
        DefaultApiResponse<ViewBalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to deposit money into the account.
     * @param deposit contains the deposit details.
     * @return the updated balance after the deposit.
     */
    @PostMapping("/make-deposit")
    public ResponseEntity<DefaultApiResponse<BalanceDto>> makeDeposit(@Validated @RequestBody DepositDto deposit){
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to withdraw money from the account.
     * @param withdraw contains the withdrawal details.
     * @return the updated balance after the withdrawal.
     */
    @PostMapping("/withdraw")
    public ResponseEntity<DefaultApiResponse<BalanceDto>> makeWithdrawal(@Validated @RequestBody WithdrawDto withdraw){
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to transfer money from one account to another.
     * @param transfer contains the transfer details.
     * @return the updated balance after the transfer.
     */
    @PostMapping("/make-transfer")
    public ResponseEntity<DefaultApiResponse<BalanceDto>> makeTransfer(@Validated @RequestBody TransferDto transfer) {
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to view the bank statement.
     * @return the bank statement for the user's account.
     */
    @GetMapping("/view-bank-statement")
    public ResponseEntity<DefaultApiResponse<?>> getBankStatement(){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to update user profile information.
     * @param updateInfo contains the updated profile information.
     * @return a response indicating the success of the update.
     */
    @PatchMapping("/update-profile")
    public ResponseEntity<DefaultApiResponse<?>> updateInformation(@RequestBody UpdateInfoDTO updateInfo){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to reset the user's password (forgotten password scenario).
     * @param passwordReset contains the password reset details.
     * @return a response indicating the success of the reset.
     */
    @PutMapping("/forgot-password")
    public ResponseEntity<DefaultApiResponse<?>> resetPassword(@RequestBody @Validated PasswordResetDto passwordReset){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to change the user's password.
     * @param passwordChange contains the password change details.
     * @return a response indicating the success of the change.
     */
    @PutMapping("/change-password")
    public ResponseEntity<DefaultApiResponse<?>> forgotPassword(@RequestBody @Validated PasswordChangeDto passwordChange){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to generate a one-time password (OneTimePassword) for the user.
     * @return a response indicating the success of OneTimePassword generation.
     */
    @GetMapping("/send-otp")
    public  ResponseEntity<DefaultApiResponse<?>> generateOTP(){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to update the transaction limit for the user's account.
     * @param transactionLimitDto contains the new transaction limit details.
     * @return a response indicating the success of the update.
     */
    @PutMapping("/update-transaction-limit")
    public ResponseEntity<DefaultApiResponse<?>> updateTransactionLimit(@RequestBody TransactionLimitDto transactionLimitDto) {
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
