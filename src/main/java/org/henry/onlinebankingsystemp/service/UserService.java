package org.henry.onlinebankingsystemp.service;

import org.apache.catalina.User;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Transaction;
import org.henry.onlinebankingsystemp.entity.TransactionType;
import org.henry.onlinebankingsystemp.entity.Users;
import org.henry.onlinebankingsystemp.otp.OTP;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.OTPRepository;
import org.henry.onlinebankingsystemp.repository.TransactionRepo;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepo transactionRepo;
    private final PasswordEncoder passwordEncoder;
    private final OTPRepository otpRepository;


    public UserService(UserRepository userRepository,
                       AccountRepository accountRepository,
                       TransactionRepo transactionRepo,
                       PasswordEncoder passwordEncoder,
                       OTPRepository otpRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.transactionRepo = transactionRepo;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.otpRepository = otpRepository;
    }

    public List<Users> getUsers(){
        return userRepository.findAll();
    }

    public Users getDetails(Long id){
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("User with id " + id + " does not exist"));
    }

    public Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((Users) authentication.getPrincipal()).getUserId();
    }

    public ResponseEntity<UserBalance> getBalance(){
        UserBalance userBalance = new UserBalance();
        Long id = getUserId();
        Users user = getDetails(id);
        userBalance.setUserId(user.getUserId());
        userBalance.setUsername(user.getUsername());
        userBalance.setBalance(user.getAccount_details().getBalance());

        return ResponseEntity.ok(userBalance);
    }

    private double getDailyTransactionAmount(Long id) {
        List<Transaction> transactions = transactionRepo.findTransactionByUsers(id);
        double totalAmount = 0;
        for(Transaction tran : transactions){
            if(tran.getTransactionType().equals(TransactionType.DEPOSIT)){
                continue;
            }
            totalAmount += tran.getAmount();
        }
        return totalAmount;
    }
    
    public ResponseEntity<Object> updateBalance(TransactionReqRes reqRes, TransactionType transactionType, String operation){
        TransactionReqRes userBalance = new TransactionReqRes();
        RequestResponse res = new RequestResponse();
        Long id = getUserId();
        Users user = getDetails(id);
        Transaction transaction = new Transaction();


        getDailyTransactionAmount(id);
        
        if(!transactionType.equals(TransactionType.DEPOSIT)){
            if(getDailyTransactionAmount(id) > user.getTransactionLimit()){
                res.setStatusCode(500);
                res.setMessage("You have exceeded your transaction limit for today");
                res.setTransactionLimit(user.getTransactionLimit());

                return ResponseEntity.ok(res);
            }
        }
        

        if(reqRes.getAmount() < 0){
            res.setStatusCode(500);
            res.setMessage("Invalid amount");
            if(reqRes.getAmount() > user.getAccount_details().getBalance()){
                res.setMessage("Insufficient Balance");
                return ResponseEntity.ok(res);
            }
            return ResponseEntity.ok(res);
        }

        if(reqRes.getDescription() == null){
            res.setStatusCode(500);
            res.setMessage("Description cannot be empty");
            return ResponseEntity.ok(res);
        }

        double newBalance;
       if(operation.equals("addition")){
           newBalance = user.getAccount_details().getBalance() + reqRes.getAmount();
       }else
           newBalance = user.getAccount_details().getBalance() - reqRes.getAmount();

        Account userAccount = user.getAccount_details();
        userAccount.setBalance(newBalance);

        transaction.setAccount(userAccount);
        transaction.setTransactionType(transactionType);
        transaction.setDateTime(new Date(System.currentTimeMillis()));
        transaction.setTargetAccountNumber(null);
        transaction.setAmount(reqRes.getAmount());
        transaction.setDescription(reqRes.getDescription());

        accountRepository.save(userAccount);
        userRepository.save(user);
        transactionRepo.save(transaction);

        //Response sent
        userBalance.setUserId(user.getUserId());
        userBalance.setUsername(user.getUsername());
        userBalance.setAmount(reqRes.getAmount());
        userBalance.setUpdated_balance(newBalance);

        return ResponseEntity.ok(userBalance);
    }

    public ResponseEntity<Object> depositMoney(TransactionReqRes reqRes){
        return updateBalance(reqRes, TransactionType.DEPOSIT, "addition");
    }

    public ResponseEntity<Object> withdrawMoney(TransactionReqRes reqRes){
        return updateBalance(reqRes, TransactionType.WITHDRAWAL, "subtract");
    }

    public Account getTarget(Long account_number){
        return accountRepository.findByAccountNumber(account_number)
                .orElseThrow(() -> new IllegalStateException("User does not exist"));
    }

    public ResponseEntity<Object> transferMoney(TransactionReqRes reqRes) {
        TransactionReqRes userBalance = new TransactionReqRes();
        RequestResponse res = new RequestResponse();

        Long id = getUserId();
        Users user = getDetails(id);
        Transaction transaction = new Transaction();

        Account targetAccount = getTarget(reqRes.getAccount_number());
        Users targetUser = getDetails(targetAccount.getUser_id());

        if(reqRes.getAmount() < 0){
            res.setStatusCode(500);
            res.setMessage("Invalid amount");
            return ResponseEntity.ok(res);
        }

        if(reqRes.getDescription() == null){
            res.setStatusCode(500);
            res.setMessage("Description cannot be empty");
            return ResponseEntity.ok(res);
        }

        double newBalance = user.getAccount_details().getBalance() - reqRes.getAmount();
        double targetBalance = targetUser.getAccount_details().getBalance() + reqRes.getAmount();

        Account userAccount = user.getAccount_details();

        userAccount.setBalance(newBalance);
        targetAccount.setBalance(targetBalance);

        transaction.setAccount(userAccount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setDateTime(new Date(System.currentTimeMillis()));
        transaction.setTargetAccountNumber(String.valueOf(reqRes.getAccount_number()));
        transaction.setAmount(reqRes.getAmount());
        transaction.setDescription(reqRes.getDescription());

        accountRepository.save(userAccount);
        accountRepository.save(targetAccount);
        userRepository.save(user);
        userRepository.save(targetUser);
        transactionRepo.save(transaction);

        //Response sent
        userBalance.setUserId(user.getUserId());
        userBalance.setUsername(user.getUsername());
        userBalance.setAmount(reqRes.getAmount());
        userBalance.setUpdated_balance(newBalance);
        userBalance.setAccount_number(reqRes.getAccount_number());

        return ResponseEntity.ok(userBalance);
    }

    public List<TransactionDTO> getTransactions(){
        Long id = getUserId();
        List<TransactionDTO> dtos = mapTransactionsToDTOs(transactionRepo.findTransactionByUsers(id));
        return dtos;
    }

    public List<TransactionDTO> mapTransactionsToDTOs(List<Transaction> transactions) {
        List<TransactionDTO> dtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionDTO dto = new TransactionDTO();
            dto.setTransactionType(transaction.getTransactionType());
            dto.setAmount(transaction.getAmount());
            dto.setDateTime(transaction.getDateTime());
            dto.setDescription(transaction.getDescription());
            if (transaction.getTargetAccountNumber() != null) {
                dto.setTargetAccountNumber(Long.valueOf(transaction.getTargetAccountNumber()));
            } else {
                dto.setTargetAccountNumber(null);
            }
            dtos.add(dto);
        }
        return dtos;
    }

    private Long generateRandomDigitNumber() {
        return new Random().nextLong(100000L);
    }

    public OTP generateOTP(){
        var otp = new OTP();

        Long id = getUserId();
        Users currentUser = getDetails(id);

        otp.setOtpCode(generateRandomDigitNumber());
        otp.setUsers(currentUser);
        otp.setExpired(false);

        long expirationTime = 240_000L;

        long currentTime = System.currentTimeMillis();
        otp.setGeneratedTime(currentTime);

        long expirationTimestamp = currentTime + expirationTime;
        otp.setExpirationTime(expirationTimestamp);

        otpRepository.save(otp);
        return otp;
    }

    public String validateOTP(Long otpCode) {
        String message;
        Long userId = getUserId();
        Users user = getDetails(userId);

        Optional<OTP> otpOptional = otpRepository.findByUsersAndOtpCode(user, otpCode);
        if (otpOptional.isPresent()) {
            OTP otp = otpOptional.get();
            long currentTime = System.currentTimeMillis();
            if (!otp.getExpired() && currentTime <= otp.getExpirationTime()) {
                message = "valid";
            } else {
                message = "expired";
            }
        } else {
            message = "invalid";
        }

        return message;
    }

    @Transactional
    public ResponseEntity<Object> updateDetails(UserInfo req){
        RequestResponse response = new RequestResponse();
        Long id = getUserId();
        Users user = getDetails(id);

        Optional<OTP> otpOptional = otpRepository.findByUsersAndOtpCode(user, req.getOtpCode());
        var otp = otpOptional.orElseThrow();


        String otpMessage = validateOTP(req.getOtpCode());
        if(otpMessage.equals("invalid")){
            response.setStatusCode(500);
            response.setMessage("Invalid OTP Code");

            return ResponseEntity.ok(response);
        }else if(otpMessage.equals("expired")){
            response.setMessage("Expired OTP Code");
            response.setStatusCode(500);
            return  ResponseEntity.ok(response);
        }

        Optional<Users> usersOptional =
                userRepository.findByEmail(req.getEmail());

        if (usersOptional.isPresent()){
            response.setStatusCode(500);
            response.setMessage("Email Already Taken");
            return ResponseEntity.ok(response);
        }

        user.setFirst_name(req.getFirstName() == null ? user.getFirst_name() : req.getFirstName());
        user.setLast_name(req.getLastName() == null ? user.getLast_name() : req.getLastName());
        user.setEmail(req.getEmail() == null ? user.getEmail() : req.getEmail());
        user.setPhone_number(req.getPhoneNumber() == null ? user.getPhone_number() : req.getPhoneNumber());

        response.setStatusCode(200);
        response.setMessage("Successfully Updated");
        otp.setExpired(true);


        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Object> validateOTP(String otpMessage, RequestResponse res) {
        if(otpMessage.equals("invalid")){
            res.setStatusCode(500);
            res.setMessage("Invalid OTP Credentials");
        }else if(otpMessage.equals("expired")){
            res.setStatusCode(500);
            res.setMessage("Expired OTP");
        }
        return ResponseEntity.ok(res);
    }

    @Transactional
    public ResponseEntity<Object> resetPassword(PasswordReset pass){
        RequestResponse res = new RequestResponse();
        Long id = getUserId();
        Users user = getDetails(id);

        Optional<OTP> otpOptional = otpRepository.findByUsersAndOtpCode(user, pass.getOtp());

        var otp = otpOptional.orElseThrow();
        String otpMessage = validateOTP(pass.getOtp());
        validateOTP(otpMessage, res);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String password = ((Users) authentication.getPrincipal()).getPassword();

        if(passwordEncoder.matches(pass.getCurrentPassword(), password)){
            user.setPassword(passwordEncoder.encode(pass.getNewPassword()));
            userRepository.save(user);
            res.setStatusCode(200);
            res.setMessage("Successfully updated Password");
            otp.setExpired(true);
        }else{
            res.setStatusCode(500);
            res.setMessage("The current password is invalid");
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.ok(res);
    }

    public ResponseEntity<Object> updateTransactionLimit(TransactionLimit transactionLimit){
        RequestResponse res = new RequestResponse();
        Long id = getUserId();
        Users user = getDetails(id);

        Optional<OTP> otpOptional = otpRepository.findByUsersAndOtpCode(user, transactionLimit.getOtp());

        var otp = otpOptional.orElseThrow();
        String otpMessage = validateOTP(transactionLimit.getOtp());
        validateOTP(otpMessage, res);

        if(transactionLimit.getAmount() < 0){
            res.setStatusCode(500);
            res.setMessage("Invalid Amount");
        }
        user.setTransactionLimit(transactionLimit.getAmount());
        res.setMessage("Successfully Updated Transaction Limit");
        res.setStatusCode(200);
        res.setTransactionLimit(transactionLimit.getAmount());
        otp.setExpired(true);

        return ResponseEntity.ok(res);
    }
}
