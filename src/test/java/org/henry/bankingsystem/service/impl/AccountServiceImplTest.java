package org.henry.bankingsystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.henry.bankingsystem.config.SecurityPasswordEncoder;
import org.henry.bankingsystem.dto.*;
import org.henry.bankingsystem.entity.Account;
import org.henry.bankingsystem.entity.Customer;
import org.henry.bankingsystem.entity.Transaction;
import org.henry.bankingsystem.enums.AccountType;
import org.henry.bankingsystem.enums.CurrencyType;
import org.henry.bankingsystem.enums.TransactionCategory;
import org.henry.bankingsystem.enums.TransactionType;
import org.henry.bankingsystem.repository.AccountRepository;
import org.henry.bankingsystem.repository.TransactionRepository;
import org.henry.bankingsystem.repository.UserRepository;
import org.henry.bankingsystem.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.henry.bankingsystem.constants.StatusCodeConstants.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityPasswordEncoder encoder;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Customer testCustomer;
    private Account testAccount;
    private String testToken;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testToken = "test-jwt-token";
        
        testCustomer = Customer.builder()
                .customerId("cust-123")
                .firstName("John")
                .lastName("Doe")
                .email(testEmail)
                .password("$2a$10$encrypted-password")
                .phoneNumber("+1234567890")
                .isSuspended(false)
                .isEnabled(true)
                .build();

        testAccount = Account.builder()
                .accountId(1L)
                .accountNumber("1234567890")
                .accountHolderName("John Doe")
                .accountType(AccountType.SAVINGS)
                .currencyType(CurrencyType.USD)
                .accountBalance(BigDecimal.valueOf(5000.00))
                .transactionLimit(BigDecimal.valueOf(10000.00))
                .hashedPin("$2a$10$hashed-pin")
                .lastTransactionDate(LocalDateTime.now())
                .customer(testCustomer)
                .transactions(new java.util.ArrayList<>()) // Initialize empty list
                .build();
    }

    @Test
    void testDepositMoney_Success() {
        // Arrange
        DepositDto depositDto = new DepositDto(BigDecimal.valueOf(1000.00));
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(accountRepository.findAccountByCustomer_Email(testEmail)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        DefaultApiResponse<ViewBalanceDto> response = accountService.depositMoney(depositDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_SUCCESS, response.getStatusCode());
        assertEquals("Account Deposit Successful", response.getStatusMessage());
        assertNotNull(response.getData());
        
        ViewBalanceDto data = response.getData();
        assertEquals(testEmail, data.email());
        assertEquals(testAccount.getAccountNumber(), data.accountNumber());
        
        // Verify interactions
        verify(accountRepository, times(2)).save(testAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testDepositMoney_InvalidAmount() {
        // Arrange
        DepositDto depositDto = new DepositDto(BigDecimal.valueOf(100.00)); // Below minimum
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);

        // Act
        DefaultApiResponse<ViewBalanceDto> response = accountService.depositMoney(depositDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_FAILED, response.getStatusCode());
        assertTrue(response.getStatusMessage().contains("Invalid Deposit Amount"));
        
        // Verify no interactions with repository
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testMakeWithdrawal_Success() {
        // Arrange
        WithdrawDto withdrawDto = new WithdrawDto(BigDecimal.valueOf(500.00), 1234L);
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userRepository.findCustomerByEmail(testEmail)).thenReturn(testCustomer);
        when(accountRepository.findAccountByCustomer_Email(testEmail)).thenReturn(Optional.of(testAccount));
        when(encoder.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(transactionRepository.findAllByCustomer_CustomerIdAndTransactionDateContains(anyString(), anyString()))
                .thenReturn(java.util.List.of());
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        DefaultApiResponse<BalanceDto> response = accountService.makeWithdrawal(withdrawDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_SUCCESS, response.getStatusCode());
        assertEquals("Account Deposit Successful", response.getStatusMessage()); // Note: message needs fixing in actual code
        assertNotNull(response.getData());
        
        BalanceDto data = response.getData();
        assertEquals(testEmail, data.getEmail());
        assertEquals(testAccount.getAccountNumber(), data.getAccountNumber());
        
        // Verify interactions
        verify(accountRepository, times(2)).save(testAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testMakeWithdrawal_InvalidPin() {
        // Arrange
        WithdrawDto withdrawDto = new WithdrawDto(BigDecimal.valueOf(500.00), 9999L);
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userRepository.findCustomerByEmail(testEmail)).thenReturn(testCustomer);
        when(accountRepository.findAccountByCustomer_Email(testEmail)).thenReturn(Optional.of(testAccount));
        when(encoder.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act
        DefaultApiResponse<BalanceDto> response = accountService.makeWithdrawal(withdrawDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_FAILED, response.getStatusCode());
        assertEquals("Invalid Account Pin", response.getStatusMessage());
        
        // Verify no save operations occurred
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testMakeTransfer_Success() {
        // Arrange
        TransferDto transferDto = new TransferDto(
                "9876543210", // receiver account number
                BigDecimal.valueOf(1000.00),
                "Test transfer",
                1234L
        );
        
        Account receiverAccount = Account.builder()
                .accountId(2L)
                .accountNumber("9876543210")
                .accountHolderName("Jane Smith")
                .accountType(AccountType.SAVINGS)
                .currencyType(CurrencyType.USD) // Same currency
                .accountBalance(BigDecimal.valueOf(2000.00))
                .customer(testCustomer)
                .transactions(new java.util.ArrayList<>()) // Initialize empty list
                .build();
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userRepository.findCustomerByEmail(testEmail)).thenReturn(testCustomer);
        when(accountRepository.findAccountByCustomer_Email(testEmail)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber("9876543210")).thenReturn(Optional.of(receiverAccount));
        when(encoder.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        DefaultApiResponse<BalanceDto> response = accountService.makeTransfer(transferDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_SUCCESS, response.getStatusCode());
        assertEquals("Transfer successful", response.getStatusMessage());
        assertNotNull(response.getData());
        
        BalanceDto data = response.getData();
        assertEquals(testEmail, data.getEmail());
        assertEquals("9876543210", data.getAccountNumber());
        
        // Verify interactions - should save both accounts and create 2 transactions
        verify(accountRepository, times(4)).save(any(Account.class)); // 2 saves + 2 saves in performTransfer
        verify(transactionRepository, times(2)).save(any(Transaction.class)); // sender + receiver transactions
    }

    @Test
    void testMakeTransfer_InsufficientFunds() {
        // Arrange
        TransferDto transferDto = new TransferDto(
                "9876543210",
                BigDecimal.valueOf(10000.00), // More than available balance
                "Test transfer",
                1234L
        );
        
        Account receiverAccount = Account.builder()
                .accountNumber("9876543210")
                .currencyType(CurrencyType.USD)
                .transactions(new java.util.ArrayList<>()) // Initialize empty list
                .build();
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userRepository.findCustomerByEmail(testEmail)).thenReturn(testCustomer);
        when(accountRepository.findAccountByCustomer_Email(testEmail)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber("9876543210")).thenReturn(Optional.of(receiverAccount));
        when(encoder.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        DefaultApiResponse<BalanceDto> response = accountService.makeTransfer(transferDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_INSUFFICIENT_FUNDS, response.getStatusCode());
        assertEquals("Insufficient Funds to make transfer", response.getStatusMessage());
        
        // Verify no transfers occurred
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testMakeTransfer_ReceiverAccountNotFound() {
        // Arrange
        TransferDto transferDto = new TransferDto(
                "9999999999", // Non-existent account
                BigDecimal.valueOf(1000.00),
                "Test transfer",
                1234L
        );
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userRepository.findCustomerByEmail(testEmail)).thenReturn(testCustomer);
        when(accountRepository.findAccountByCustomer_Email(testEmail)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber("9999999999")).thenReturn(Optional.empty());

        // Act
        DefaultApiResponse<BalanceDto> response = accountService.makeTransfer(transferDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_INVALID_ACCOUNT, response.getStatusCode());
        assertEquals("Receiver Account Not Found", response.getStatusMessage());
    }

    @Test
    void testMakeTransfer_SelfTransfer() {
        // Arrange
        TransferDto transferDto = new TransferDto(
                testAccount.getAccountNumber(), // Same account number
                BigDecimal.valueOf(1000.00),
                "Test transfer",
                1234L
        );
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userRepository.findCustomerByEmail(testEmail)).thenReturn(testCustomer);
        when(accountRepository.findAccountByCustomer_Email(testEmail)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber(testAccount.getAccountNumber())).thenReturn(Optional.of(testAccount));

        // Act
        DefaultApiResponse<BalanceDto> response = accountService.makeTransfer(transferDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_INVALID_ACCOUNT, response.getStatusCode());
        assertEquals("Account Number belongs to Customer: Request Invalid", response.getStatusMessage());
    }

    @Test
    void testCheckBalance_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractClaims(eq(testToken), any())).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findAccountByCustomer_CustomerId(testCustomer.getCustomerId()))
                .thenReturn(Optional.of(testAccount));

        // Act
        DefaultApiResponse<ViewBalanceDto> response = accountService.checkBalance();

        // Assert
        assertNotNull(response);
        assertEquals(SUCCESS, response.getStatusCode());
        assertEquals("Customer Balance", response.getStatusMessage());
        assertNotNull(response.getData());
        
        ViewBalanceDto data = response.getData();
        assertEquals(testEmail, data.email());
        assertEquals(testAccount.getAccountNumber(), data.accountNumber());
        assertEquals(testAccount.getAccountBalance(), data.balance());
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto(
                "currentPassword",
                "newPassword123",
                "newPassword123"
        );
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);
        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testCustomer));
        when(encoder.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.matches("currentPassword", testCustomer.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newencoded");
        when(userRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        DefaultApiResponse<?> response = accountService.changePassword(passwordChangeDto);

        // Assert
        assertNotNull(response);
        assertEquals(SUCCESS, response.getStatusCode());
        assertEquals("Password changed successfully", response.getStatusMessage());
        
        verify(userRepository, times(1)).save(testCustomer);
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        // Arrange
        PasswordChangeDto passwordChangeDto = new PasswordChangeDto(
                "currentPassword",
                "newPassword123",
                "differentPassword" // Mismatch
        );
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtService.isTokenExpired(testToken)).thenReturn(false);

        // Act
        DefaultApiResponse<?> response = accountService.changePassword(passwordChangeDto);

        // Assert
        assertNotNull(response);
        assertEquals(TRANSACTION_FAILED, response.getStatusCode());
        assertEquals("New password and confirm password do not match", response.getStatusMessage());
        
        verify(userRepository, never()).save(any());
    }
}