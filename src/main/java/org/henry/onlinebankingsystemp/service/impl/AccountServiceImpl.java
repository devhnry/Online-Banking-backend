package org.henry.onlinebankingsystemp.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.exceptions.ResourceNotFoundException;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.henry.onlinebankingsystemp.constants.StatusCodeConstants.*;

@Slf4j @Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final HttpServletRequest request;

    private String CUSTOMER_ACCESS_TOKEN(){
        return request.getHeader("Authorization").substring(7);
    }

    @Override
    public DefaultApiResponse<CustomerDto> getDetails() {
        // Verify if the customer access token is expired
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());

        // Extract user email from the JWT token
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());
        log.info("Extracted user email from token: {}", userEmail);

        DefaultApiResponse<CustomerDto> apiResponse = new DefaultApiResponse<>();

        // Fetch customer details from the database
        Customer customer = userRepository.findByEmail(userEmail).orElseThrow(
                () -> {
                    log.error("Customer with email {} does not exist", userEmail);
                    return new ResourceNotFoundException(String.format("Customer with email %s does not exist", userEmail));
                });

        // Fetch account details associated with the customer
        Account account = accountRepository.findAccountByCustomer_CustomerId(customer.getCustomerId()).orElseThrow(
                () -> {
                    log.error("Account related to Customer with id {} does not exist", customer.getCustomerId());
                    return new ResourceNotFoundException(String.format("Account related to Customer with id (%s) does not exist", customer.getCustomerId()));
                });

        // Map account details to AccountDto
        AccountDto accountData = new AccountDto();
        accountData.setAccountId(account.getAccountId());
        accountData.setAccountHolderName(account.getAccountHolderName());
        accountData.setAccountNumber(account.getAccountNumber());
        accountData.setAccountType(account.getAccountType());
        accountData.setCurrencyType(account.getCurrencyType());
        accountData.setBalance(account.getAccountBalance());

        // Map customer and account details to CustomerDto
        CustomerDto customerData = CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .fullName(String.format("%s %S", customer.getFirstName(), customer.getLastName()))
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .accountDetails(accountData)
                .build();

        // Set API response details
        apiResponse.setStatusCode(SUCCESS);
        apiResponse.setStatusMessage("Customer details");
        apiResponse.setData(customerData);

        log.info("Customer details retrieved successfully for email: {}", userEmail);

        return apiResponse;
    }

    @Override
    public DefaultApiResponse<ViewBalanceDto> checkBalance() {
        // Verify if the customer access token is expired
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());

        DefaultApiResponse<ViewBalanceDto> apiResponse = new DefaultApiResponse<>();
        String userEmail = jwtService.extractClaims(CUSTOMER_ACCESS_TOKEN(), Claims::getSubject);

        Customer existingCustomer;
        Account existingAccount;
        Optional<Customer> customer = userRepository.findByEmail(userEmail);

        try {
            if (customer.isPresent()) {
                existingCustomer = customer.get();
                log.info("Customer found: {}", existingCustomer.getCustomerId());

                Optional<Account> account = accountRepository.findAccountByCustomer_CustomerId(existingCustomer.getCustomerId());

                if (account.isPresent()) {
                    existingAccount = account.get();
                    log.info("Account found: {}", existingAccount.getAccountId());

                    // Set API response details
                    apiResponse.setStatusCode(SUCCESS);
                    apiResponse.setStatusMessage("Customer Balance");

                    // Prepare balance data
                    String lastUpdatedAt = LocalDateTime.now().toString().replace("T", " ").substring(0, 16);
                    ViewBalanceDto balance = new ViewBalanceDto(
                            existingCustomer.getEmail(), existingAccount.getAccountNumber(),
                            existingAccount.getAccountBalance(), lastUpdatedAt
                    );
                    apiResponse.setData(balance);

                    log.info("Balance details retrieved successfully for account: {}", existingAccount.getAccountNumber());
                } else {
                    log.warn("Account not found for customer: {}", existingCustomer.getCustomerId());
                }
            } else {
                log.warn("Customer not found for email: {}", userEmail);
            }
        } catch (RuntimeException e) {
            log.error("An Error occurred while trying to fetch balance: {}", e.getMessage());
        }
        return apiResponse;
    }

    private void verifyTokenExpiration(String token) {
        if (jwtService.isTokenExpired(token)) {
            log.warn("Token has expired");
            throw new ExpiredJwtException(null, null, "Access Token has expired");
        }
    }


//    public DefaultApiResponse<BalanceDto> transferMoneyToCustomer(TransferDto requestBody) {
//
//        Transaction transaction = new Transaction();
//        DefaultApiResponse<BalanceDto> res = new DefaultApiResponse<>();
//
//        Customer customer = getCurrentUser.get();
//        Account userAccount = customer.getAccount();
//        String targetAccountNumber = request.getTargetAccountNumber();
//
//        Account targetAccount = getTarget(targetAccountNumber);
//        Customer targetCustomer = getDetails(targetAccount.getCustomerId());
//
//        if(request.getAmount().compareTo(BigDecimal.valueOf(200)) < 0){
//            res.setStatusCode(500);
//            res.setStatusMessage("Can't transfer less than 200 NGN");
//            return res;
//        }
//
//        if(request.getAmount().compareTo(customer.getAccount().getBalance()) > 0){
//            res.setStatusCode(500);
//            res.setStatusMessage("Insufficient Balance");
//            return res;
//        }
//
//        transaction.setCustomer(customer);
//        targetAccount.setBalance(targetCustomer.getAccount().getBalance().add(request.getAmount()));
//        transaction.setAccount(targetAccount);
//        transaction.setTransactionType(TransactionType.TRANSFER);
//        transaction.setTransactionDate(MillisToDateTime());
//        transaction.setTargetAccountNumber(String.valueOf(request.getAmount()));
//        transaction.setAmount(request.getAmount());
//        transaction.setDebit(request.getAmount());
//        transaction.setCredit(null);
//        transaction.setRunningBalance(request.getAmount());
//        transaction.setTransactionRef(generator.generateReference());
//        userAccount.setBalance(customer.getAccount().getBalance().subtract(request.getAmount()));
//
//        userBalance.setUsername(customer.getUsername());
//        userBalance.setBalance(customer.getAccount().getBalance().subtract(request.getAmount()));
//
//        accountRepository.save(userAccount);
//        accountRepository.save(targetAccount);
//        userRepository.save(customer);
//        userRepository.save(targetCustomer);
//        transactionRepository.save(transaction);
//
//        res.setStatusCode(200);
//        res.setStatusMessage("Transfer Successful");
//
//        return res;
//    }
//
//    public BigDecimal getDailyTransactionAmount(Long id) {
//        List<Transaction> transactions = transactionRepository.findTransactionByCustomer(id);
//        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
//        for(Transaction tran : transactions){
//            if(tran.getTransactionType().equals(TransactionType.DEPOSIT)){
//                continue;
//            }
//            totalAmount = totalAmount.add(tran.getAmount());
//        }
//        return totalAmount;
//    }
//
//    public DefaultApiResponse updateBalance(TransactionDTO request, TransactionType transactionType, String operation){
//        DefaultApiResponse res = new DefaultApiResponse();
//        try {
//            BalanceDto userBalance = new BalanceDto();
//            Customer customer = getCurrentUser.get();
//
//            log.info("Comparing Balance and amount returned");
//            if(request.getAmount().compareTo(BigDecimal.ZERO) < 0){
//                res.setStatusCode(500);
//                res.setStatusMessage("Invalid amount");
//                return res;
//            }
//
//            int b1 = request.getAmount().compareTo(customer.getAccount().getBalance());
//            boolean b2 = request.getAmount().compareTo(customer.getAccount().getBalance()) == -1;
//            boolean b3 = request.getAmount().compareTo(customer.getAccount().getBalance()) < 0;
//            boolean b4 = request.getAmount().compareTo(customer.getAccount().getBalance()) > 0;
//            boolean b5 = request.getAmount().compareTo(customer.getAccount().getBalance()) == 1;
//
//            log.info("Checking for adequate balance");
//            if(request.getAmount().compareTo(customer.getAccount().getBalance()) != -1 && transactionType == TransactionType.WITHDRAWAL){
//                res.setStatusCode(500);
//                res.setStatusMessage("Insufficient Balance");
//                return res;
//            }
//
//            log.info("Performing Transaction Limit Check");
//            if(transactionType != TransactionType.DEPOSIT){
//                if(getDailyTransactionAmount(customer.getCustomerId()).add(request.getAmount()).compareTo(customer.getAccount().getTransactionLimit()) > 0){
//                    res.setStatusCode(500);
//                    res.setStatusMessage("You have exceeded your transaction limit for today");
//                    return res;
//                }
//            }
//
//            BigDecimal newBalance;
//            if(operation.equals("addition")){
//                newBalance = customer.getAccount().getBalance().add(request.getAmount());
//            }else
//                newBalance = customer.getAccount().getBalance().subtract(request.getAmount());
//
//            log.info("Updating the Database");
//            Account userAccount = customer.getAccount();
//            userAccount.setBalance(newBalance);
//
//            Transaction transaction = new Transaction();
//            transaction.setCustomer(customer);
//            transaction.setAccount(userAccount);
//            transaction.setTransactionType(transactionType);
//            transaction.setTransactionDate(MillisToDateTime());
//            transaction.setTargetAccountNumber(null);
//            transaction.setAmount(request.getAmount());
//            transaction.setBalanceAfterRunningBalance(newBalance);
//            if(transactionType.equals(TransactionType.DEPOSIT)){
//                transaction.setCredit(request.getAmount());
//            }else {
//                transaction.setDebit(request.getAmount());
//            }
//            transaction.setRunningBalance(request.getAmount());
//            transaction.setTransactionRef(generator.generateReference());
//
//
//            userBalance.setUsername(customer.getUsername());
//            userBalance.setBalance(newBalance);
//
//            accountRepository.save(userAccount);
//            userRepository.save(customer);
//            transactionRepository.save(transaction);
//
//            res.setStatusCode(200);
//            res.setStatusMessage(transactionType == TransactionType.WITHDRAWAL ? "Withdrawal Successful" : "Deposit Successful");
//
//            return res;
//        } catch (Exception e) {
//            res.setStatusCode(500);
//            res.setStatusMessage(e.getMessage());
//            return res;
//        }
//    }
//
//    public DefaultApiResponse depositMoney(TransactionDTO request){
//        return updateBalance(request, TransactionType.DEPOSIT, "addition");
//    }
//
//    public DefaultApiResponse withdrawMoney(TransactionDTO request){
//        return updateBalance(request, TransactionType.WITHDRAWAL, "subtract");
//    }
}
