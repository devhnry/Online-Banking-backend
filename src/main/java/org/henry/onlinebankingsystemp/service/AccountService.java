package org.henry.onlinebankingsystemp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.dto.BalanceDTO;
import org.henry.onlinebankingsystemp.dto.DefaultResponse;
import org.henry.onlinebankingsystemp.dto.TransactionDTO;
import org.henry.onlinebankingsystemp.dto.TransferDTO;
import org.henry.onlinebankingsystemp.dto.enums.TransactionType;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.entity.Transaction;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.TransactionRepo;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.utils.AccountNumberGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Supplier;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepo transactionRepo;
    private final UserRepository userRepository;
    private final AccountNumberGenerator generator;

    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((Customer) authentication.getPrincipal()).getCustomerId();
    }
    public Customer getDetails(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Customer with id " + id + " does not exist"));
    }
    private final Supplier<Customer> getCurrentUser = () -> { Long id = getUserId(); Customer customer = getDetails(id);
        return customer;
    };

    private Account getTarget(String number){
        log.info("Fetching Account Number");
        return accountRepository.findByAccountNumber(number)
                .orElseThrow(() -> new IllegalStateException("Account does not exist"));
    }

    private LocalDateTime MillisToDateTime() {
            long millis = System.currentTimeMillis();
            Instant instant = Instant.ofEpochMilli(millis);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return localDateTime;
    }

    public DefaultResponse transferMoney(TransferDTO request) {
        BalanceDTO userBalance = new BalanceDTO();
        Transaction transaction = new Transaction();
        DefaultResponse res = new DefaultResponse();

        Customer customer = getCurrentUser.get();
        Account userAccount = customer.getAccount();
        String targetAccountNumber = request.getTargetAccountNumber();

        Account targetAccount = getTarget(targetAccountNumber);
        Customer targetCustomer = getDetails(targetAccount.getCustomerId());

        if(request.getAmount().compareTo(BigDecimal.valueOf(200)) < 0){
            res.setStatusCode(500);
            res.setMessage("Can't transfer less than 200 NGN");
            return res;
        }

        if(request.getAmount().compareTo(customer.getAccount().getBalance()) > 0){
            res.setStatusCode(500);
            res.setMessage("Insufficient Balance");
            return res;
        }

        transaction.setCustomer(customer);
        targetAccount.setBalance(targetCustomer.getAccount().getBalance().add(request.getAmount()));
        transaction.setAccount(targetAccount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionDate(MillisToDateTime());
        transaction.setTargetAccountNumber(String.valueOf(request.getAmount()));
        transaction.setAmount(request.getAmount());
        transaction.setDebit(request.getAmount());
        transaction.setCredit(null);
        transaction.setRunningBalance(request.getAmount());
        transaction.setTransactionRef(generator.generateReference());
        userAccount.setBalance(customer.getAccount().getBalance().subtract(request.getAmount()));

        userBalance.setUsername(customer.getUsername());
        userBalance.setBalance(customer.getAccount().getBalance().subtract(request.getAmount()));

        accountRepository.save(userAccount);
        accountRepository.save(targetAccount);
        userRepository.save(customer);
        userRepository.save(targetCustomer);
        transactionRepo.save(transaction);

        res.setStatusCode(200);
        res.setMessage("Transfer Successful");

        return res;
    }

    private BigDecimal getDailyTransactionAmount(Long id) {
        List<Transaction> transactions = transactionRepo.findTransactionByCustomer(id);
        BigDecimal totalAmount = BigDecimal.valueOf(0.0);
        for(Transaction tran : transactions){
            if(tran.getTransactionType().equals(TransactionType.DEPOSIT)){
                continue;
            }
            totalAmount = totalAmount.add(tran.getAmount());
        }
        return totalAmount;
    }

    public DefaultResponse updateBalance(TransactionDTO request, TransactionType transactionType, String operation){
        DefaultResponse res = new DefaultResponse();
        try {
            BalanceDTO userBalance = new BalanceDTO();
            Customer customer = getCurrentUser.get();
            Transaction transaction = new Transaction();

            getDailyTransactionAmount(customer.getCustomerId());

            log.info("Performing Transaction Limit Check");
            if(!transactionType.equals(TransactionType.DEPOSIT)){
                if(getDailyTransactionAmount(customer.getCustomerId()).compareTo(customer.getAccount().getTransactionLimit()) > 0){
                    res.setStatusCode(500);
                    res.setMessage("You have exceeded your transaction limit for today");
                    return res;
                }
            }

            log.info("Comparing Balance and amount returned");
            if(request.getAmount().compareTo(BigDecimal.valueOf(0)) < 0){
                res.setStatusCode(500);
                res.setMessage("Invalid amount");
                if(request.getAmount().compareTo(customer.getAccount().getBalance()) > 0){
                    res.setMessage("Insufficient Balance");
                }
                return res;
            }

            BigDecimal newBalance;
            if(operation.equals("addition")){
                newBalance = customer.getAccount().getBalance().add(request.getAmount());
            }else
                newBalance = customer.getAccount().getBalance().subtract(request.getAmount());

            log.info("Updating the Database");
            Account userAccount = customer.getAccount();
            userAccount.setBalance(newBalance);

            transaction.setCustomer(customer);
            transaction.setAccount(userAccount);
            transaction.setTransactionType(transactionType);
            transaction.setTransactionDate(MillisToDateTime());
            transaction.setTargetAccountNumber(null);
            transaction.setAmount(request.getAmount());
            transaction.setBalanceAfterRunningBalance(newBalance);
            if(transactionType.equals(TransactionType.DEPOSIT)){
                transaction.setCredit(request.getAmount());
            }else {
                transaction.setDebit(request.getAmount());
            }
            transaction.setRunningBalance(request.getAmount());
            transaction.setTransactionRef(generator.generateReference());

            //Response sent
            userBalance.setUsername(customer.getUsername());
            userBalance.setBalance(newBalance);

            accountRepository.save(userAccount);
            userRepository.save(customer);
            transactionRepo.save(transaction);

            res.setStatusCode(200);
            res.setMessage(transactionType == TransactionType.WITHDRAWAL ? "Withdrawal Successful" : "Deposit Successful");

            return res;
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
            return res;
        }
    }

    public DefaultResponse depositMoney(TransactionDTO request){
        return updateBalance(request, TransactionType.DEPOSIT, "addition");
    }

    public DefaultResponse withdrawMoney(TransactionDTO request){
        return updateBalance(request, TransactionType.WITHDRAWAL, "subtract");
    }
}


