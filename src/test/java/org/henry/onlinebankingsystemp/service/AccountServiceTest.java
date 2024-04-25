package org.henry.onlinebankingsystemp.service;

import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.dto.DefaultResponse;
import org.henry.onlinebankingsystemp.dto.TransferDTO;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.TransactionRepo;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.utils.AccountNumberGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
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
    private static final Account account = new Account();

    void initiateCustomerAndAccount(){
        request.setTargetAccountNumber(TARGET_ACCOUNT_NUMBER);
        currentUser.setCustomerId(1L);
        account.setBalance(BigDecimal.valueOf(3000L));
        account.setCustomerId(currentUser.getCustomerId());
        currentUser.setAccount(account);

        // Mocking authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Getting User");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(currentUser));

        log.info("Retrieving account from User");
        given(accountRepository.findByAccountNumber(TARGET_ACCOUNT_NUMBER)).willReturn(Optional.of(account));
    }

    @Test
    public void transferMoney() {

        initiateCustomerAndAccount();

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
        log.info("Getting User");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(currentUser));

        log.info("Retrieving account from User");
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
    @Disabled
    void updateBalance() {
    }

    @Test
    @Disabled
    void depositMoney() {
    }

    @Test
    @Disabled
    void withdrawMoney() {
    }
}