package org.henry.onlinebankingsystemp.controller;

import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Transaction;
import org.henry.onlinebankingsystemp.entity.Users;
import org.henry.onlinebankingsystemp.otp.OTP;
import org.henry.onlinebankingsystemp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "{user_id}")
    private Users getDetails(@PathVariable("user_id") Long id) {
        return userService.getDetails(id);
    }

    @GetMapping
    public List<Users> getUsers(){
        return userService.getUsers();
    }

    @GetMapping(path = "/balance")
    public ResponseEntity<UserBalance> getBalance(){
        return userService.getBalance();
    }

    @PostMapping(path = "/deposit")
    public ResponseEntity<Object> depositMoney(@RequestBody TransactionReqRes req){
        return userService.depositMoney(req);
    }

    @PostMapping(path = "/withdraw")
    public ResponseEntity<Object> withdrawMoney(@RequestBody TransactionReqRes req){
        return userService.withdrawMoney(req);
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity<Object> transferMoney(@RequestBody TransactionReqRes req){
        return userService.transferMoney(req);
    }

    @GetMapping(path = "/transactions")
    public List<TransactionDTO> viewTransactions(){
        return userService.getTransactions();
    }

    @PatchMapping(path = "/update")
    public ResponseEntity<Object> updateInformation(@RequestBody UserInfo req){
        return userService.updateDetails(req);
    }

    @PutMapping(path = "/resetpassword")
    public ResponseEntity<Object> resetPassword(@RequestBody PasswordReset pass){
        return userService.resetPassword(pass);
    }

    @GetMapping(path = "/generateotp")
    public OTP generateOTP(){
        return userService.generateOTP();
    }

    @PutMapping(path = "/updatetransactionlimit")
    public ResponseEntity<Object> updateTransactionLimit(@RequestBody TransactionLimit transactionLimit) {
        return userService.updateTransactionLimit(transactionLimit);
    }

}
