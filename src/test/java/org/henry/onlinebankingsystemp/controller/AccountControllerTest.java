package org.henry.onlinebankingsystemp.controller;

import org.henry.onlinebankingsystemp.dto.BalanceDTO;
import org.henry.onlinebankingsystemp.dto.DefaultResponse;
import org.henry.onlinebankingsystemp.dto.TransactionDTO;
import org.henry.onlinebankingsystemp.dto.UpdateInfoDTO;
import org.henry.onlinebankingsystemp.dto.enums.Role;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.entity.OTP;
import org.henry.onlinebankingsystemp.repository.TokenRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.henry.onlinebankingsystemp.service.UserDetailService;
import org.henry.onlinebankingsystemp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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

    @BeforeEach()
    void setUp(){
        currentUser.setCustomerId(1L);
        currentUser.setFirstName("Henry");
        currentUser.setLastName("Taiwo");
        currentUser.setRole(Role.USER);
    }

    @Test
    void notNull(){
        assertNotNull(jwtService);
        assertNotNull(accountService);
        assertNotNull(userRepository);
        assertNotNull(userDetailService);
        assertNotNull(tokenRepository);
        assertNotNull(restTemplate);
    }

    @Test
    void willReturnBalanceForAuthorisedPersonnel() throws Exception {

        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(new BigDecimal(2000));
        balanceDTO.setUsername("email@gmail.com");
        given(userService.getBalance()).willReturn(balanceDTO);

        this.mockMvc
                .perform(get(baseUrl + "balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(currentUser)))
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

    @Test
    void willAllowAuthorisedPersonnelToMakeDeposit() throws Exception {
        String requestBody =
                """
                {
                "amount" : 100000
                }
                """;
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(BigDecimal.valueOf(100000));

        DefaultResponse res = new DefaultResponse();
        res.setStatusCode(200);
        res.setMessage("Deposit Successful");

        given(accountService.depositMoney(transactionDTO)).willReturn(res);
        this.mockMvc
                .perform(post(baseUrl + "deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf().asHeader())
                        .with(user(currentUser))
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        verify(accountService).depositMoney(transactionDTO);
    }

    @Test
    void willAllowAuthorisedPersonnelToUpdateInformation() throws Exception{
        String requestBody =
                """
                {
                  "firstName" : "Beyonce",
                  "lastName" : "Jam",
                  "email" : "jam@gmail.com",
                  "otpCode" : 56702
                }
                """;
        UpdateInfoDTO info = new UpdateInfoDTO();
        info.setFirstName("Beyonce");
        info.setLastName("Jam");
        info.setEmail("jam@gmail.com");
        info.setOtpCode(56702L);

        DefaultResponse res = new DefaultResponse();
        res.setStatusCode(200);
        res.setMessage("Update Successful");

        given(userService.updateDetails(info)).willReturn(res);
        this.mockMvc
                .perform(patch(baseUrl + "updateProfile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf().asHeader())
                        .with(user(currentUser))
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(userService).updateDetails(info);
    }
}

