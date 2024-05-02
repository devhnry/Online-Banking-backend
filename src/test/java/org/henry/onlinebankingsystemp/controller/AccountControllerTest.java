package org.henry.onlinebankingsystemp.controller;

import org.hamcrest.Matchers;
import org.henry.onlinebankingsystemp.dto.BalanceDTO;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

@WebMvcTest(AccountController.class)
@Import(WebSecurity.class)
class AccountControllerTest {

    @MockBean private AccountService accountService;
    @MockBean private UserService userService;
    @Autowired private MockMvc mockMvc;

    private static String baseUrl = "/api/v1/account/";

    @Test
    @WithMockUser(username = "email@gmail.com")
    void willReturnBalanceForAuthorisedPersonnel() throws Exception {

        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(new BigDecimal(2000));
        balanceDTO.setUsername("email@gmail.com");

        given(userService.getBalance()).willReturn(balanceDTO);

        this.mockMvc
            .perform(get(baseUrl + "balance")
                    .contentType(MediaType.APPLICATION_JSON)
//                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(new BigDecimal(2000)))
                .andDo(MockMvcResultHandlers.print());

        verify(userService).getBalance();
    }

    @Test
    void depositMoney() {
    }

    @Test
    void withdrawMoney() {
    }

    @Test
    void transferMoney() {
    }

    @Test
    void viewTransactions() {
    }

    @Test
    void updateInformation() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void generateOTP() {
    }

    @Test
    void updateTransactionLimit() {
    }
}