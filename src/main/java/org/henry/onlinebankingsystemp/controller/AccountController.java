package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final AccountService accountService;

    @GetMapping("/view-balance")
    public ResponseEntity<DefaultApiResponse<ViewBalanceDto>> getBalance(){
        DefaultApiResponse<ViewBalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/make-deposit")
    public ResponseEntity<DefaultApiResponse<BalanceDto>> makeDeposit(@Validated @RequestBody DepositDto deposit){
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<DefaultApiResponse<BalanceDto>> makeWithdrawal(@Validated @RequestBody WithdrawDto withdraw){
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/make-transfer")
    public ResponseEntity<DefaultApiResponse<BalanceDto>> makeTransfer(@Validated @RequestBody TransferDto transfer) {
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/view-bank-statement")
    public ResponseEntity<DefaultApiResponse<?>> getBankStatement(){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<DefaultApiResponse<?>> updateInformation(@RequestBody UpdateInfoDTO updateInfo){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<DefaultApiResponse<?>> resetPassword(@RequestBody @Validated PasswordResetDto passwordReset){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<DefaultApiResponse<?>> forgotPassword(@RequestBody @Validated PasswordChangeDto passwordChange){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/send-otp")
    public  ResponseEntity<DefaultApiResponse<?>> generateOTP(){
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update-transaction-limit")
    public ResponseEntity<DefaultApiResponse<?>> updateTransactionLimit(@RequestBody TransactionLimitDto transactionLimitDto) {
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
