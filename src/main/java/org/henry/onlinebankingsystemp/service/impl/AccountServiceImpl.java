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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
                    ViewBalanceDto balance = new ViewBalanceDto(
                            existingCustomer.getEmail(), existingAccount.getAccountNumber(),
                            existingAccount.getAccountBalance(), getLastUpdatedAt()
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
            throw new RuntimeException(e.getMessage());
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

        // Data initialization
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());
        DefaultApiResponse<ViewBalanceDto> response = new DefaultApiResponse<>();
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());

        try {
            log.info("Checking if Amount is Valid");
            if(requestBody.amountToDeposit().compareTo(BigDecimal.ZERO) < 0 || requestBody.amountToDeposit().compareTo(BigDecimal.valueOf(500)) < 0){
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Invalid Deposit Amount: ( Amount needs to be 500 upwards )");
                return response;
            }

            log.info("Performing Deposit into Account");
            Optional<Account> customerAccount = accountRepository.findAccountByCustomer_Email(userEmail);
            if(customerAccount.isPresent()){
                account = customerAccount.get();
                account.setAccountBalance(account.getAccountBalance().add(requestBody.amountToDeposit()));
                account.setLastTransactionDate(LocalDateTime.now());
                accountRepository.save(account);
                log.info("Account has been successfully credited");
            }

            Transaction newTransaction = getTransaction(account, requestBody.amountToDeposit(), TransactionCategory.CREDIT, TransactionType.DEPOSIT);
            transactionRepository.save(newTransaction);

            account.getTransactions().add(newTransaction);
            accountRepository.save(account);

            ViewBalanceDto data = new ViewBalanceDto(
                    userEmail, account.getAccountNumber(),
                    account.getAccountBalance().setScale(2, RoundingMode.CEILING), account.getLastTransactionDate().toString());

            log.info("Account Deposit Was Successful");
            response.setStatusCode(TRANSACTION_SUCCESS);
            response.setStatusMessage("Account Deposit Successful");
            response.setData(data);

            return response;

        } catch (Exception e) {
            log.error("An Error occurred while trying to CREDIT balance: {}", e.getMessage());
            response.setStatusCode(TRANSACTION_FAILED);
            response.setStatusMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public DefaultApiResponse<BalanceDto> makeWithdrawal(WithdrawDto requestBody) {
        // Data initialization
        Account account = new Account();
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());
        DefaultApiResponse<BalanceDto> response = new DefaultApiResponse<>();
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());
        Customer customer = userRepository.findCustomerByEmail(userEmail);

        try {
            log.info("Performing Withdrawal from Account");
            Optional<Account> customerAccount = accountRepository.findAccountByCustomer_Email(userEmail);
            if(customerAccount.isPresent()){
                account = customerAccount.get();
                account.setAccountBalance(account.getAccountBalance().subtract(requestBody.amountToWithdraw()
                        .add(calculateCharges(requestBody.amountToWithdraw(), account))));
                account.setLastTransactionDate(LocalDateTime.now());
                accountRepository.save(account);
            }

            log.info("Validating Transaction pin");
            if(!encoder.passwordEncoder().matches(String.valueOf(requestBody.hashedPin()), account.getHashedPin())){
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Invalid Account Pin");
                log.info("Invalid Account Pin to perform transaction");
                return response;
            }

            log.info("Performing Check for Transaction Limit for Account");
            BigDecimal totalTransactionAmount = getTotalTransactionAmountForToday(customer.getCustomerId()).add(requestBody.amountToWithdraw());
            if(totalTransactionAmount.compareTo(account.getTransactionLimit()) > 0){
                response.setStatusCode(TRANSACTION_LIMIT_EXCEEDED);
                response.setStatusMessage("Transaction Limit Exceeded");
                log.info("Transaction Limit Exceeded to perform transaction");
                return response;
            }

            Transaction newTransaction = getTransaction(account, requestBody.amountToWithdraw(), TransactionCategory.CREDIT, TransactionType.WITHDRAWAL);
            transactionRepository.save(newTransaction);

            account.getTransactions().add(newTransaction);
            accountRepository.save(account);

            BalanceDto data = BalanceDto.builder()
                    .email(customer.getEmail())
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getAccountBalance().setScale(2, RoundingMode.CEILING))
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
            log.info("Retrieving Sender and Receiver Account");
            // Step 1: Retrieve Sender Account
            Optional<Account> senderAccount = accountRepository.findAccountByCustomer_Email(userEmail);
            if (senderAccount.isPresent()) {
               existingSenderaccount = senderAccount.get();
            }else{
                log.error("Sender Account not found");
                throw new ResourceNotFoundException("Customer Account Not Found");
            }

            // Step 2: Retrieve Receiver Account
            Optional<Account> receiverAccount = accountRepository.findByAccountNumber(requestBody.accountNumber());
            if (receiverAccount.isPresent()) {
                existingReceiveraccount = receiverAccount.get();
            }else{
                log.error("Receiver Account not found");
                response.setStatusCode(TRANSACTION_INVALID_ACCOUNT);
                response.setStatusMessage("Receiver Account Not Found");
                return response;
            }

            // Ensures that eh user does not make transfer to themselves
            if(existingReceiveraccount.getAccountNumber().equals(existingSenderaccount.getAccountNumber())){
                response.setStatusCode(TRANSACTION_INVALID_ACCOUNT);
                response.setStatusMessage("Account Number belongs to Customer: Request Invalid");
                return response;
            }

            // Validate that both Account are of the same currency
            log.info("Checking if both Accounts are under the same Currency");
            if(!existingReceiveraccount.getCurrencyType().equals(existingSenderaccount.getCurrencyType())){
                log.info("Accounts are of different Currencies: ({} and {})", existingSenderaccount, existingReceiveraccount);
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage(String.format("Accounts are of different Currencies: (%s and %s)", existingSenderaccount, existingReceiveraccount));
                return response;
            }


            // Step 3: Validate Amount & Description
            log.info("Validating Amount");
            if (requestBody.amount().compareTo(BigDecimal.valueOf(50)) <= 0) {
                log.info("Invalid Amount passed in PayLoad");
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Invalid Transfer amount: Must be above 50");
                return response;
            }

            log.info("Validating Descriptions");
            if (requestBody.description() == null || requestBody.description().trim().isEmpty()) {
                response.setStatusCode(TRANSACTION_FAILED);
                response.setStatusMessage("Description cannot be empty");
                return response;
            }

            // Step 4: Validate PIN
            if(validateHashedPin(existingSenderaccount, String.valueOf(requestBody.hashedPin()))) {

                // Step 5: Check for Insufficient Balance
                log.info("Checking if Account has Sufficient Balance to make transactions.");
                if(requestBody.amount().compareTo(existingSenderaccount.getAccountBalance()) > 0){
                    response.setStatusCode(TRANSACTION_INSUFFICIENT_FUNDS);
                    response.setStatusMessage("Insufficient Funds to make transfer");
                    return response;
                }

                // Step 6: Perform the Transfer
                performTransfer(existingReceiveraccount, existingSenderaccount, requestBody.amount());

            }else {
                log.info("Incorrect Pin to perform transfer operation");
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
            log.error("Unexpected Error Occurred while Making Transfer: {}", e.getMessage());
            response.setStatusCode(GENERIC_ERROR);
            response.setStatusMessage("Unexpected Error Occurred: " + e.getMessage());
        }
        return response;
    }

    /**
         Method to get Transfer Summary
         Would be sent to the Frontend for a Modal, User can view charges being added
     * */
    @Override
    public DefaultApiResponse<TransactionSummaryDto> displayTransferSummary(TransferDto requestBody) {
        verifyTokenExpiration(CUSTOMER_ACCESS_TOKEN());
        Account existingSenderaccount;
        DefaultApiResponse<TransactionSummaryDto> response = new DefaultApiResponse<>();
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());

        try {
            log.info("Retrieving Sender Account...");
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
            log.info("Populating Summary Data for Transaction");
            TransactionSummaryDto summary = new TransactionSummaryDto(
                    transferAmount,
                    charges.setScale(2, RoundingMode.CEILING),
                    totalAmount.setScale(2, RoundingMode.CEILING)
            );

            response.setStatusCode(SUCCESS);
            response.setStatusMessage("Transfer summary calculated successfully");
            response.setData(summary);
            log.info("Summary Data for Transaction Sent Successfully");
        } catch (RuntimeException e) {
            log.error("Unexpected Error Occurred while fetching Transfer Summary: {}", e.getMessage());
            response.setStatusCode(GENERIC_ERROR);
            response.setStatusMessage("Unexpected Error Occurred: " + e.getMessage());
        }
        return response;
    }

    /**
        Method to get Account Holder Name
        Would be sent to the Frontend for Customer to validate Account.
    * */
    @Override
    public String getAccountHolderName(String accountNumber){
        String userEmail = jwtService.extractUsername(CUSTOMER_ACCESS_TOKEN());
        Optional<Account> senderAccount = accountRepository.findAccountByCustomer_Email(userEmail);
        if(senderAccount.isPresent()){
            Account account = senderAccount.get();
            if(account.getAccountNumber().equals(accountNumber)){
                return "Bad Request";
            }
        }

        log.info("Getting Account Holder Name for Account Number: {}", accountNumber);
        Account existingAccount = new Account();
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            existingAccount = account.get();
        }
        return existingAccount.getAccountHolderName();
    }

    /** Method to update Balance and Transaction Data for each Account belonging to the customer */
    private void performTransfer(Account existingSenderaccount, Account existingReceiveraccount, BigDecimal amount) {
        log.info("Performing Transfer Transactions");
        BigDecimal totalAmount = calculateCharges(amount, existingSenderaccount);
        existingSenderaccount.setAccountBalance(existingSenderaccount.getAccountBalance().subtract(totalAmount));
        existingReceiveraccount.setAccountBalance(existingReceiveraccount.getAccountBalance().add(amount));

        existingReceiveraccount.setLastTransactionDate(LocalDateTime.now());
        existingReceiveraccount.setLastTransactionDate(LocalDateTime.now());

        accountRepository.save(existingSenderaccount);
        accountRepository.save(existingReceiveraccount);

        // Generates transaction Data for Sender and Receiver and saves it
        Transaction senderTransaction = getTransaction(existingSenderaccount, amount, TransactionCategory.DEBIT, TransactionType.TRANSFER);
        Transaction receiverTransaction = getTransaction(existingReceiveraccount, amount, TransactionCategory.CREDIT, TransactionType.TRANSFER);

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);

        existingReceiveraccount.getTransactions().add(receiverTransaction);
        existingSenderaccount.getTransactions().add(senderTransaction);

        accountRepository.save(existingReceiveraccount);
        accountRepository.save(existingSenderaccount);
        log.info("Transfer transaction successful");
    }

    // Takes in the Amount, Account, Transaction Category and Type to formulate a Transaction Data
    private Transaction getTransaction(Account account, BigDecimal amount, TransactionCategory category, TransactionType type) {
        log.info("Populating transaction data for account {}", account.getAccountNumber());
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
                .transactionDate(getLastUpdatedAt())
                .balanceBeforeTransaction(value)
                .balanceAfterTransaction(account.getAccountBalance())
                .targetAccountNumber(account.getAccountNumber())
                .build();
    }

    // Gets the Date and Time of that Day and return it as a string
    private String getLastUpdatedAt(){
        return LocalDateTime.now().toString().replace("T", " ").substring(0, 16);
    }

    // Some Mock Data for Bank Charges
    private BigDecimal calculateCharges(BigDecimal amount, Account account) {
        log.info("Calculating charges for account {}", account.getAccountNumber());
        String currencyType = String.valueOf(account.getCurrencyType());
        switch (currencyType) {
            // Bank Charges for USD Currency
            case "USD":
                amount = amount.multiply(BigDecimal.valueOf(0.1));
                if(amount.compareTo(BigDecimal.valueOf(100_000)) < 0){
                    amount = amount.multiply(BigDecimal.valueOf(0.3));
                }
            // Bank Charges for NGN Currency
            case "NGN":
                amount = amount.multiply(BigDecimal.valueOf(0.2));
                if(amount.compareTo(BigDecimal.valueOf(100_000)) < 0){
                    amount = amount.multiply(BigDecimal.valueOf(0.4));
                }
            // Bank Charges for EUR Currency
            case "EUR":
                amount = amount.multiply(BigDecimal.valueOf(0.25));
                if(amount.compareTo(BigDecimal.valueOf(100_000)) < 0){
                    amount = amount.multiply(BigDecimal.valueOf(0.45));
                }
        }
        return amount;
    }


    /** Validate Account Hashed Pin */
    private boolean validateHashedPin(@NotNull Account account, String hashedPin) {
        log.info("Validating hashed pin for account {}", account.getAccountNumber());
        return encoder.passwordEncoder().matches(hashedPin, account.getHashedPin());
    }

    /** Generates Transaction Reference for Customer */
    private String generateTransactionRef() {
        log.info("Generating transaction ref for transactions");
        String transactionReference;
        do{
            transactionReference = UUID.randomUUID().toString().substring(0,12).replace("-","");
        }while(transactionRepository.existsByTransactionRef(transactionReference));
        return transactionReference;
    }


    /* Fetch Transactions of the User for that particular Day and Sums it up*/
    private BigDecimal getTotalTransactionAmountForToday(String id) {
        log.info("Calculating Total Transaction Amount of {} for Customer with id: {}", LocalDateTime.now() , id);
        String TODAY = LocalDate.now().toString();
        List<Transaction> transactions = transactionRepository.findAllByCustomer_CustomerIdAndTransactionDateContains(id, TODAY);
        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
        for(Transaction transaction : transactions){
            if(transaction.getTransactionType().equals(TransactionType.DEPOSIT)){
                continue;
            }
            totalAmount = totalAmount.add(transaction.getAmount());
            log.info("Total Amount: {}" ,totalAmount);
        }
        return totalAmount;
    }
}
