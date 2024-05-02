package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.OTP;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.UserService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
    private final AccountService accountService;

    @GetMapping("/balance")
    public BalanceDTO getBalance(){
        return userService.getBalance();
    }

    @PostMapping("/deposit")
    public DefaultResponse depositMoney(@RequestBody TransactionDTO req){
        return accountService.depositMoney(req);
    }

    @PostMapping("/withdraw")
    public DefaultResponse withdrawMoney(@RequestBody TransactionDTO req){
        return accountService.withdrawMoney(req);
    }

    @PostMapping("/transfer")
    public DefaultResponse transferMoney(@RequestBody TransferDTO req){
        return accountService.transferMoney(req);
    }

    @GetMapping("/statement")
    public List<TransactionDTO> viewTransactions(){
        return userService.viewStatement();
    }

    @PatchMapping("/updateProfile")
    public DefaultResponse updateInformation(@RequestBody UpdateInfoDTO req){
        return userService.updateDetails(req);
    }

    @PutMapping("/resetPassword")
    public DefaultResponse resetPassword(@RequestBody PasswordResetDTO pass){
        return userService.resetPassword(pass);
    }

    @GetMapping("/generateOtp")
    public OTP generateOTP(){
        return userService.generateOTP();
    }

    @PutMapping("/transactionLimit")
    public DefaultResponse updateTransactionLimit(@RequestBody TransactionLimit transactionLimit) {
        return userService.updateTransactionLimit(transactionLimit);
    }
}
