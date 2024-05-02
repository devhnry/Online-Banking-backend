package org.henry.onlinebankingsystemp.service;

import lombok.extern.slf4j.Slf4j;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepo transactionRepo;
    @Mock private AccountNumberGenerator generator;

    @InjectMocks private AccountService underTest;
    @Captor private ArgumentCaptor argumentCaptor;

    private static final String TARGET_ACCOUNT_NUMBER = "234323452";
    private static final TransferDTO request = new TransferDTO();
    private static final Customer currentUser = new Customer();
    private static Account account = new Account();
    private static Transaction transaction = new Transaction();

    void initiateCustomerAndAccount() {
        request.setTargetAccountNumber(TARGET_ACCOUNT_NUMBER);
        currentUser.setCustomerId(1L);
        account.setTransactionLimit(BigDecimal.valueOf(200000.00));
        account.setBalance(BigDecimal.valueOf(3000L));
        account.setCustomerId(currentUser.getCustomerId());
        currentUser.setAccount(account);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(currentUser));
    }

    @Test
    public void transferMoney() {
        initiateCustomerAndAccount();
        given(accountRepository.findByAccountNumber(TARGET_ACCOUNT_NUMBER)).willReturn(Optional.of(account));

        request.setAmount(BigDecimal.valueOf(500)); // Assuming transfer amount is valid
        DefaultResponse response = underTest.transferMoney(request);

        // Verify
        assertEquals(200, response.getStatusCode());
        assertEquals("Transfer Successful", response.getMessage());
    }

    @Test
    void cannotFindUserById(){
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.getDetails(currentUser.getCustomerId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Customer with id " + currentUser.getCustomerId() + " does not exist");
    }

    @Test
    @DisplayName("Cannot Transfer less than 200 NGN")
    void invalidAmount(){
        initiateCustomerAndAccount();
        given(accountRepository.findByAccountNumber(TARGET_ACCOUNT_NUMBER)).willReturn(Optional.of(account));

        request.setAmount(BigDecimal.valueOf(3000)); // Assuming transfer amount is valid
        account.setBalance(BigDecimal.valueOf(2500L));

        DefaultResponse response = underTest.transferMoney(request);
        assertEquals(500, response.getStatusCode());
        assertEquals("Insufficient Balance", response.getMessage());
    }

    @Test
    void willReturnInsufficientBalance(){
        initiateCustomerAndAccount();
        log.info("Getting User");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(currentUser));

        log.info("Retrieving account from User");
        given(accountRepository.findByAccountNumber(TARGET_ACCOUNT_NUMBER)).willReturn(Optional.of(account));
        request.setAmount(BigDecimal.valueOf(150)); // Assuming transfer amount is valid

        DefaultResponse response = underTest.transferMoney(request);
        assertEquals(500, response.getStatusCode());
        assertEquals("Can't transfer less than 200 NGN", response.getMessage());
    }

    @Test
    void willReturnZeroForDailyTransactionAmountIfEmptyList(){
        List<Transaction> transactions = new ArrayList<>();

        given(transactionRepo.findTransactionByCustomer(currentUser.getCustomerId())).willReturn(transactions);
        BigDecimal result = underTest.getDailyTransactionAmount(currentUser.getCustomerId());

        assertEquals(BigDecimal.valueOf(0.0), result);
    }

    @Test
    void willReturnDailyTransactionAmount(){
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(300.0));
        transaction.setTransactionType(TransactionType.TRANSFER);

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(BigDecimal.valueOf(300.0));
        transaction2.setTransactionType(TransactionType.WITHDRAWAL);

        Transaction transaction3 = new Transaction();
        transaction3.setAmount(BigDecimal.valueOf(300.0));
        transaction3.setTransactionType(TransactionType.DEPOSIT);

        List<Transaction> transactions = List.of(transaction, transaction2, transaction3);

        given(transactionRepo.findTransactionByCustomer(currentUser.getCustomerId())).willReturn(transactions);
        BigDecimal result = underTest.getDailyTransactionAmount(currentUser.getCustomerId());
        assertEquals(BigDecimal.valueOf(600.0), result);
    }

    @Test
    void updateBalanceForTransactionTypeDeposit() {
        initiateCustomerAndAccount();
        TransactionDTO request = new TransactionDTO();
        request.setAmount(BigDecimal.valueOf(200));
        DefaultResponse response = underTest.updateBalance(request, TransactionType.DEPOSIT, "addition");
        assertEquals(200, response.getStatusCode());
        assertEquals("Deposit Successful", response.getMessage());
    }

    @Test
    void updateBalanceForTransactionTypeWithdraw() {
        initiateCustomerAndAccount();
        TransactionDTO request = new TransactionDTO();
        request.setAmount(BigDecimal.valueOf(600.00));
        DefaultResponse response = underTest.updateBalance(request, TransactionType.WITHDRAWAL, "subtract");
        log.info(response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertEquals("Withdrawal Successful", response.getMessage());
    }

    @Test
    void willReturnTransactionLimitMessage(){
        initiateCustomerAndAccount();
        Transaction transaction = new Transaction();
        TransactionDTO request = new TransactionDTO();

        transaction.setAmount(new BigDecimal("200000.00"));
        transaction.setAccount(account);
        transaction.setCustomer(currentUser);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        currentUser.setAccount(account);
        request.setAmount(BigDecimal.valueOf(2500.00));
        request.setTargetAccountNumber(TARGET_ACCOUNT_NUMBER);
        given(transactionRepo.findTransactionByCustomer(currentUser.getCustomerId())).willReturn(List.of(transaction));

        DefaultResponse response = underTest.updateBalance(request, TransactionType.WITHDRAWAL, "subtract");
        log.info(response.getMessage());
        assertEquals(500, response.getStatusCode());
        assertEquals("You have exceeded your transaction limit for today", response.getMessage());
    }

    @Test
    void willReturnInvalidAmountIfGivenNegativeAmount(){
        initiateCustomerAndAccount();
        account.setTransactionLimit(BigDecimal.valueOf(200000.00));
        TransactionDTO request = new TransactionDTO();
        request.setAmount(BigDecimal.valueOf(-300.00));

        DefaultResponse response = underTest.updateBalance(request, TransactionType.WITHDRAWAL, "subtract");
        assertEquals(500, response.getStatusCode());
        assertEquals("Invalid amount", response.getMessage());
    }


    @Test
    void willReturnInsufficientBalanceIfGivenMoreThanBalance(){
        initiateCustomerAndAccount();
        account.setTransactionLimit(BigDecimal.valueOf(200000.00));
        TransactionDTO request = new TransactionDTO();
        request.setAmount(BigDecimal.valueOf(3500.00));

        DefaultResponse response = underTest.updateBalance(request, TransactionType.WITHDRAWAL, "subtract");
        assertEquals(500, response.getStatusCode());
        assertEquals("Insufficient Balance", response.getMessage());
    }
}