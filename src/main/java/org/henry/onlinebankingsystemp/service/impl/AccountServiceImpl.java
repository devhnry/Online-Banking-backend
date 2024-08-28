package org.henry.onlinebankingsystemp.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.config.SecurityPasswordEncoder;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.entity.Transaction;
import org.henry.onlinebankingsystemp.enums.TransactionCategory;
import org.henry.onlinebankingsystemp.enums.TransactionType;
import org.henry.onlinebankingsystemp.exceptions.ResourceNotFoundException;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.TransactionRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.henry.onlinebankingsystemp.constants.StatusCodeConstants.*;

@Slf4j @Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final HttpServletRequest request;
    private final SecurityPasswordEncoder encoder;

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
        log.info("Mapping Customer Details to DTO");
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
        log.info("Verifying Access Token");
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

    /* Method to Verify Token Expiration */
    private void verifyTokenExpiration(String token) {
        if (jwtService.isTokenExpired(token)) {
            log.warn("Token has expired");
            throw new ExpiredJwtException(null, null, "Access Token has expired");
        }
    }

    @Override
    public DefaultApiResponse<ViewBalanceDto> depositMoney(DepositDto requestBody){
        Account account = new Account();

        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());
        DefaultApiResponse<ViewBalanceDto> response = new DefaultApiResponse<>();
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());
        Customer customer = userRepository.findCustomerByEmail(userEmail);

        try {
            log.info("Checking if Amount is Valid");
            if(requestBody.amountToDeposit().compareTo(BigDecimal.ZERO) < 0 || requestBody.amountToDeposit().compareTo(BigDecimal.valueOf(500)) < 0){
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Invalid Deposit Amount: ( Amount needs to be 500 upwards )");
                return response;
            }

            Optional<Account> customerAccount = accountRepository.findAccountByCustomer_Email(userEmail);
            if(customerAccount.isPresent()){
                account = customerAccount.get();
                account.setAccountBalance(account.getAccountBalance().add(requestBody.amountToDeposit()));
                account.setLastTransactionDate(LocalDateTime.now());
                accountRepository.save(account);
            }

            Transaction newTransaction = getTransaction(account, requestBody.amountToDeposit(), TransactionCategory.CREDIT, TransactionType.DEPOSIT);
            transactionRepository.save(newTransaction);

            account.getTransactions().add(newTransaction);
            accountRepository.save(account);

            ViewBalanceDto data = new ViewBalanceDto(
                    userEmail, account.getAccountNumber(),
                    account.getAccountBalance(), account.getLastTransactionDate().toString());

            log.info("Account Deposit Was Successful");
            response.setStatusCode(TRANSACTION_SUCCESS);
            response.setStatusMessage("Account Deposit Successful");
            response.setData(data);

            return response;

        } catch (Exception e) {
            response.setStatusCode(TRANSACTION_FAILED);
            response.setStatusMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public DefaultApiResponse<BalanceDto> makeWithdrawal(WithdrawDto requestBody) {
        Account account = new Account();
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());
        Customer customer = userRepository.findCustomerByEmail(userEmail);

        try {
            Optional<Account> customerAccount = accountRepository.findAccountByCustomer_Email(userEmail);
            if(customerAccount.isPresent()){
                account = customerAccount.get();
                account.setAccountBalance(account.getAccountBalance().subtract(requestBody.amountToWithdraw()
                        .add(calculateCharges(requestBody.amountToWithdraw(), account))));
                account.setLastTransactionDate(LocalDateTime.now());
                accountRepository.save(account);
            }

            if(!encoder.passwordEncoder().matches(String.valueOf(requestBody.hashedPin()), account.getHashedPin())){
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Invalid Account Pin");
                return response;
            }

            BigDecimal totalTransactionAmount = getTotalTransactionAmountForToday(customer.getCustomerId());
            if(totalTransactionAmount.compareTo(account.getTransactionLimit()) > 0){
                response.setStatusCode(TRANSACTION_LIMIT_EXCEEDED);
                response.setStatusMessage("Transaction Limit Exceeded");
                return response;
            }

            Transaction newTransaction = getTransaction(account, requestBody.amountToWithdraw(), TransactionCategory.CREDIT, TransactionType.WITHDRAWAL);
            transactionRepository.save(newTransaction);

            account.getTransactions().add(newTransaction);
            accountRepository.save(account);

            BalanceDto data = BalanceDto.builder()
                    .email(customer.getEmail())
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getAccountBalance())
                    .requestType(TransactionCategory.DEBIT.toString())
                    .lastUpdatedAt(getLastUpdatedAt())
                    .build();

            log.info("Account Withdrawal Was Successful");
            response.setStatusCode(TRANSACTION_SUCCESS);
            response.setStatusMessage("Account Deposit Successful");
            response.setData(data);

            return response;

        }catch (RuntimeException e){
            response.setStatusCode(TRANSACTION_FAILED);
            response.setStatusMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public DefaultApiResponse<BalanceDto> makeTransfer(TransferDto requestBody) {
        Account existingSenderaccount;
        Account existingReceiveraccount;
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());
        Customer customer = userRepository.findCustomerByEmail(userEmail);

        try {
            // Step 1: Retrieve Sender Account
            Optional<Account> senderAccount = accountRepository.findAccountByCustomer_Email(userEmail);
            if (senderAccount.isPresent()) {
               existingSenderaccount = senderAccount.get();
            }else{
                throw new ResourceNotFoundException("Customer Account Not Found");
            }

            // Step 2: Retrieve Receiver Account
            Optional<Account> receiverAccount = accountRepository.findByAccountNumber(requestBody.accountNumber());
            if (receiverAccount.isPresent()) {
                existingReceiveraccount = receiverAccount.get();
            }else{
                response.setStatusCode(TRANSACTION_INVALID_ACCOUNT);
                response.setStatusMessage("Receiver Account Not Found");
                return response;
            }

            // Validate that both Account are of the same currency
            if(!existingReceiveraccount.getCurrencyType().equals(existingSenderaccount.getCurrencyType())){
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage(String.format("Accounts are of different Currencies: (%s and %s)", existingSenderaccount, existingReceiveraccount));
                return response;
            }

            // Step 3: Validate Amount & Description
            if (requestBody.amount().compareTo(BigDecimal.valueOf(50)) <= 0) {
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Invalid Transfer amount: Must be above 50");
                return response;
            }

            if (requestBody.description() == null || requestBody.description().trim().isEmpty()) {
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Description cannot be empty");
                return response;
            }

            // Step 4: Validate PIN
            if(validateHashedPin(existingSenderaccount, String.valueOf(requestBody.hashedPin()))) {

                // Step 5: Check for Insufficient Balance
                if(requestBody.amount().compareTo(existingSenderaccount.getAccountBalance()) > 0){
                    response.setStatusCode(TRANSACTION_INSUFFICIENT_FUNDS);
                    response.setStatusMessage("Insufficient Funds to make transfer");
                    return response;
                }

                // Step 6: Perform the Transfer
                performTransfer(existingReceiveraccount, existingSenderaccount, requestBody.amount());

            }else {
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Incorrect Pin to perform transfer operation");

                return response;
            }

            response.setStatusCode(TRANSACTION_SUCCESS);
            response.setStatusMessage("Transfer successful");
            BalanceDto dataBalance = BalanceDto.builder()
                    .email(customer.getEmail())
                    .requestType(String.valueOf(TransactionCategory.DEBIT))
                    .amount(requestBody.amount())
                    .accountNumber(requestBody.accountNumber())
                    .balance(existingSenderaccount.getAccountBalance().setScale(2, RoundingMode.CEILING))
                    .description(requestBody.description())
                    .lastUpdatedAt(getLastUpdatedAt())
                    .build();
            response.setData(dataBalance);

        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException(e.getMessage());
        }catch (RuntimeException e) {
            response.setStatusCode(GENERIC_ERROR);
            response.setStatusMessage("Unexpected Error Occurred: " + e.getMessage());
        }
        return response;
    }

    @Override
    public DefaultApiResponse<TransactionSummaryDto> displayTransferSummary(TransferDto requestBody) {
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());
        Account existingSenderaccount;
        DefaultApiResponse<TransactionSummaryDto> response = new DefaultApiResponse<>();
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());

        try {
            // Step 1: Retrieve Sender Account
            Optional<Account> senderAccount = accountRepository.findAccountByCustomer_Email(userEmail);
            if (senderAccount.isPresent()) {
                existingSenderaccount = senderAccount.get();
            }else{
                throw new ResourceNotFoundException("Customer Account Not Found");
            }

            // Step 2: Calculate Charges and Total Amount
            BigDecimal transferAmount = requestBody.amount();
            BigDecimal charges = calculateCharges(transferAmount, existingSenderaccount);
            BigDecimal totalAmount = transferAmount.add(charges);

            // Step 3: Transfer Summary DTO to display Data.
            TransactionSummaryDto summary = new TransactionSummaryDto(
                    transferAmount, 
                    charges.setScale(2, RoundingMode.CEILING),
                    totalAmount.setScale(2, RoundingMode.CEILING)
            );

            response.setStatusCode(SUCCESS);
            response.setStatusMessage("Transfer summary calculated successfully");
            response.setData(summary);

        } catch (RuntimeException e) {
            response.setStatusCode(GENERIC_ERROR);
            response.setStatusMessage("Unexpected Error Occurred: " + e.getMessage());
        }
        return response;
    }

    @Override
    public String getAccountHolderName(String accountNumber){
        Account existingAccount = new Account();
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            existingAccount = account.get();
        }
        return existingAccount.getAccountHolderName();
    }

    private void performTransfer(Account existingSenderaccount, Account existingReceiveraccount, BigDecimal amount) {
        BigDecimal totalAmount = calculateCharges(amount, existingSenderaccount);
        existingSenderaccount.setAccountBalance(existingSenderaccount.getAccountBalance().subtract(totalAmount));
        existingReceiveraccount.setAccountBalance(existingReceiveraccount.getAccountBalance().add(amount));

        existingReceiveraccount.setLastTransactionDate(LocalDateTime.now());
        existingReceiveraccount.setLastTransactionDate(LocalDateTime.now());

        accountRepository.save(existingSenderaccount);
        accountRepository.save(existingReceiveraccount);

        Transaction senderTransaction = getTransaction(existingSenderaccount, amount, TransactionCategory.DEBIT, TransactionType.TRANSFER);
        Transaction receiverTransaction = getTransaction(existingReceiveraccount, amount, TransactionCategory.CREDIT, TransactionType.TRANSFER);

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);

        existingReceiveraccount.getTransactions().add(receiverTransaction);
        existingSenderaccount.getTransactions().add(senderTransaction);

        accountRepository.save(existingReceiveraccount);
        accountRepository.save(existingSenderaccount);

    }

    private Transaction getTransaction(Account account, BigDecimal amount, TransactionCategory category, TransactionType type) {
        BigDecimal value;
        if(category == TransactionCategory.CREDIT){
            value = account.getAccountBalance().subtract(amount);
        }else{
            value = account.getAccountBalance().add(amount);
        }

        return Transaction.builder()
                .transactionRef(generateTransactionRef())
                .customer(account.getCustomer())
                .account(account)
                .transactionType(type)
                .transactionCategory(category)
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .balanceBeforeTransaction(value)
                .balanceAfterTransaction(account.getAccountBalance())
                .targetAccountNumber(account.getAccountNumber())
                .build();
    }

    private String getLastUpdatedAt(){
        return LocalDateTime.now().toString().replace("T", " ").substring(0, 16);
    }

    // Some Mock Data for Bank Charges
    private BigDecimal calculateCharges(BigDecimal amount, Account account) {
        String currencyType = String.valueOf(account.getCurrencyType());
        switch (currencyType) {
            case "USD":
                amount = amount.multiply(BigDecimal.valueOf(0.2));
                if(amount.compareTo(BigDecimal.valueOf(100_000)) < 0){
                    amount = amount.multiply(BigDecimal.valueOf(0.6));
                }
            case "NGN":
                amount = amount.multiply(BigDecimal.valueOf(0.5));
                if(amount.compareTo(BigDecimal.valueOf(100_000)) < 0){
                    amount = amount.multiply(BigDecimal.valueOf(1.5));
                }
            case "EUR":
                amount = amount.multiply(BigDecimal.valueOf(0.35));
                if(amount.compareTo(BigDecimal.valueOf(100_000)) < 0){
                    amount = amount.multiply(BigDecimal.valueOf(0.9));
                }
        }
        return amount;
    }


    /* Validate Account Hashed Pin*/
    private boolean validateHashedPin(Account account, String hashedPin) {
        log.info(String.valueOf(encoder.passwordEncoder().matches(hashedPin, account.getHashedPin())));
        return encoder.passwordEncoder().matches(hashedPin, account.getHashedPin());
    }

    /* Generates Transaction Reference for Customer */
    private String generateTransactionRef() {
        String transactionReference;
        do{
            transactionReference = UUID.randomUUID().toString().substring(0,12).replace("-","");
        }while(transactionRepository.existsByTransactionRef(transactionReference));
        return transactionReference;
    }

    private BigDecimal getTotalTransactionAmountForToday(String id) {
        List<Transaction> transactions = transactionRepository.findAllByCustomer_CustomerId(id);
        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
        for(Transaction transaction : transactions){
            if(transaction.getTransactionType().equals(TransactionType.DEPOSIT)){
                continue;
            }
            totalAmount = totalAmount.add(transaction.getAmount());
        }
        return totalAmount;
    }
}
