package org.henry.onlinebankingsystemp.controller;

import org.henry.onlinebankingsystemp.dto.BalanceDTO;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.repository.TokenRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.henry.onlinebankingsystemp.service.UserDetailService;
import org.henry.onlinebankingsystemp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(AccountController.class)
@Import(WebSecurity.class)
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @MockBean private AccountService accountService;
    @Mock private UserRepository userRepository;
    @MockBean private UserService userService;
    @MockBean private JWTService jwtService;
    @MockBean private UserDetailService userDetailService;
    @MockBean private TokenRepository tokenRepository;
    @MockBean private RestTemplate restTemplate;
    @Autowired private MockMvc mockMvc;
    @InjectMocks private AccountController accountController;

    private static final Customer currentUser = new Customer();
    private static String baseUrl = "/api/v1/account/";

    @Test
    void willReturnBalanceForAuthorisedPersonnel() throws Exception {

        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(new BigDecimal(2000));
        balanceDTO.setUsername("email@gmail.com");
        given(userService.getBalance()).willReturn(balanceDTO);

        this.mockMvc
                .perform(get(baseUrl + "balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("email@gmail.com").password("password").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(new BigDecimal(2000)))
                .andDo(MockMvcResultHandlers.print());

        verify(userService).getBalance();
    }

    @Test
    void willReturnBalanceForUnAuthorisedPersonnel() throws Exception {

        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(new BigDecimal(2000));
        balanceDTO.setUsername("email@gmail.com");
        given(userService.getBalance()).willReturn(balanceDTO);

        this.mockMvc
                .perform(get(baseUrl + "balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());

        verifyNoInteractions(userService);
    }
}