package org.henry.onlinebankingsystemp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.TokenRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.AuthenticationService;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service @Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final static BigDecimal DEFAULT_TRANSACTION_LIMIT = BigDecimal.valueOf(200_000.00);
    private final static BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(4);

    @Override
    public DefaultApiResponse<SuccessfulOnboardDto> onBoard(OnboardUserDto requestBody) {
        DefaultApiResponse<SuccessfulOnboardDto> response = new DefaultApiResponse<>();
        SuccessfulOnboardDto responseData = new SuccessfulOnboardDto();

        Account newAccount = new Account();
        Customer newCustomer = new Customer();

        boolean customerAlreadyExists = userRepository.existsByEmail(requestBody.email());

        /* Checks if the Customer Already exist: Prompts the user to Log in */
        if (customerAlreadyExists) {
            response.setStatusCode(200);
            response.setStatusMessage("Customer Already Exists: Try Logging in");
            return response;
        }

        // Checks if the initial Deposit is up to the Required Amount
        if(!(requestBody.initialDeposit().compareTo(BigDecimal.valueOf(5000.00)) >= 0)){
            response.setStatusCode(400);
            response.setStatusMessage("An account need ot be created with an Initial Deposit of MIN 5000.");
            return response;
        }

        // Calls Method to Generate Account and Customer
        generateCustomerAndAccount(newCustomer, newAccount, requestBody);
        responseData = setResponseData(newAccount, newCustomer);

        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatusMessage("Customer Successfully Onboarded");
        response.setData(responseData);

        return response;
    }

    @Override
    public DefaultApiResponse<AuthorisationResponseDto> login(LoginRequestDto requestBody) {
        return null;
    }

    @Override
    public DefaultApiResponse<AuthorisationResponseDto> refreshToken(RefreshTokenDto requestBody) {
        return null;
    }

    /* Verifies the password Strength during ONBOARD and Login. */
    private DefaultApiResponse<SuccessfulOnboardDto> checkPassword(String password) {
        DefaultApiResponse<SuccessfulOnboardDto> response = new DefaultApiResponse<>();
        if (!verifyPasswordStrength(password)) {
            log.info("Password not strong enough");
            response.setStatusCode(500);
            response.setStatusMessage("Password should contain at least 8 characters, numbers and a symbol");
        }
        return response;
    }

    /* Generates Account Number for Customer */
    private String generateAccountNumber() {
        log.info("Generating Account Number for Customer");
        long uniqueValue = userRepository.count();
        String uniqueNumber = "";
        /*
        * Generates a random number from the characters
        * Adds the uniqueValue to the end of uniqueNumber which changes as number of users increases
        */
        uniqueNumber = RandomStringUtils.random((int) (10 - uniqueValue), "0123456789");
        return uniqueNumber + uniqueValue;
    }

    private void generateCustomerAndAccount(Customer newCustomer, Account newAccount, OnboardUserDto requestBody){
        String accountNumber = generateAccountNumber();
        String fullName = String.format("%s %s", requestBody.firstName(), requestBody.lastName());

        // Generates new Account from the RequestBody
        log.info("Generating Account for Customer");
        try {
            newAccount = Account.builder()
                    .accountNumber(accountNumber)
                    .accountHolderName(fullName)
                    .accountType(requestBody.accountType())
                    .balance(requestBody.initialDeposit())
                    .transactionLimit(DEFAULT_TRANSACTION_LIMIT)
                    .dateOpened(Instant.now())
                    .isActive(true)
                    .currencyType(requestBody.currencyType())
                    .interestRate(DEFAULT_INTEREST_RATE)
                    .lastTransactionDate(Instant.now())
                    .build();
            accountRepository.save(newAccount);
            log.info("Account Generated: {}", newAccount);
        } catch (RuntimeException ex) {
            log.error("Error generating account for Customer {}", ex.getMessage());
        }

        // Stores the Account in a List and saves it to the customer
        List<Account> relatedAccounts = new ArrayList<>();
        relatedAccounts.add(newAccount);

        // Generates new Customer from the RequestBody
        log.info("Generating Customer Details for Customer");
        try {
            newCustomer = Customer.builder()
                    .firstName(requestBody.firstName())
                    .lastName(requestBody.lastName())
                    .username(requestBody.email())
                    .email(requestBody.email())
                    .password(passwordEncoder.encode(requestBody.password()))
                    .phoneNumber(requestBody.phoneNumber())
                    .isSuspended(false)
                    .accounts(relatedAccounts)
                    .build();
            userRepository.save(newCustomer);
            log.info("Customer created successfully: {}", newCustomer);
        } catch (RuntimeException ex) {
            log.error("Error generating customer entity {}", ex.getMessage());
        }
    }

    // Method to Assign New Customer and Account to SuccessOnboardDto Response
    private SuccessfulOnboardDto setResponseData(Account newAccount, Customer newCustomer) {
        SuccessfulOnboardDto responseData = new SuccessfulOnboardDto();

        // Creates a Mock Value of the account
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(newAccount.getAccountId());
        accountDto.setAccountHolderName(newAccount.getAccountHolderName());
        accountDto.setAccountNumber(newAccount.getAccountNumber());
        accountDto.setAccountType(newAccount.getAccountType());
        accountDto.setCurrencyType(newAccount.getCurrencyType());
        accountDto.setBalance(newAccount.getBalance());

        // Creates Response Data Body
        responseData.setCustomerId(newCustomer.getCustomerId());
        responseData.setFirstName(newCustomer.getFirstName());
        responseData.setLastName(newCustomer.getLastName());
        responseData.setEmail(newCustomer.getEmail());
        responseData.setPhoneNumber(newCustomer.getPhoneNumber());
        responseData.setAccount(accountDto);

        return responseData;
    }

    private static boolean verifyPasswordStrength(String password) {
        log.info("Verifying password strength");
        if(password.length()>=8) {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            log.info("Password strength validation passed!");
            return hasLetter.find() && hasDigit.find() && hasSpecial.find();
        }
        log.info("Password strength validation failed!");
        return false;
    }


//
//    public LoginResponseDTO login(LoginRequestDto request) {
//        LoginResponseDTO res = new LoginResponseDTO();
//        log.info("Performing Authentication");
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//
//            Optional<Admin> optionalAdmin = adminRepository.findByEmail(request.getEmail());
//            if (optionalAdmin.isPresent()) {
//                var admin = optionalAdmin.orElseThrow();
//                var jwtToken = jwtService.generateToken(admin);
//                jwtService.generateRefreshToken(new HashMap<>(), admin);
//
//                res.setStatusCode(200);
//                res.setEmail(request.getEmail());
//                res.setToken(jwtToken);
//                res.setRefreshToken(jwtToken);
//                res.setExpirationTime("24hr");
//                res.setMessage("Successfully Logged In");
//                log.info(res.getMessage());
//
//                revokeAllAdminTokens(admin);
//                saveAdminToken(admin, jwtToken);
//            } else {
//                boolean optionalUser = userRepository.findByEmail(request.getEmail()).isPresent();
//                if(!optionalUser){
//                    res.setStatusCode(404);
//                    res.setMessage("Customer or Admin not found");
//                    return res;
//                }
//                var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
//                if(user.getIsSuspended()){
//                    res.setMessage("Your account has been suspended");
//                    res.setStatusCode(500);
//                    return res;
//                }
//
//                var jwtToken = jwtService.generateToken(user);
//                jwtService.generateRefreshToken(new HashMap<>(), user);
//
//                res.setStatusCode(200);
//                res.setEmail(request.getEmail());
//                res.setToken(jwtToken);
//                res.setRefreshToken(jwtToken);
//                res.setExpirationTime("24hr");
//                res.setMessage("Successfully Signed In");
//                log.info("Logged In Successfully");
//
//                revokeAllUserTokens(user);
//                saveUserToken(user, jwtToken);
//            }
//        } catch (Exception e) {
//            res.setStatusCode(500);
//            res.setMessage(e.getMessage());
//        }
//        return res;
//    }
//
//    public LoginResponseDTO refreshToken(RefreshTokenDto refreshTokenRequest){
//        LoginResponseDTO res = new LoginResponseDTO();
//        String userEmail = jwtService.extractUsername(refreshTokenRequest.getToken());
//        Customer customer = userRepository.findByEmail(userEmail).orElseThrow();
//
//        try {
//            Optional<Customer> optionalUser = userRepository.findByEmail(userEmail);
//            if(optionalUser.isPresent()){
//                if(jwtService.isTokenValid(refreshTokenRequest.getToken(), customer)){
//                    var newToken = jwtService.generateToken(customer);
//
//                    revokeAllUserTokens(customer);
//                    saveUserToken(customer,newToken);
//
//                    res.setStatusCode(200);
//                    res.setToken(newToken);
//                    res.setRefreshToken(refreshTokenRequest.getToken());
//                    res.setExpirationTime("24hr");
//                    res.setMessage("Successfully Refreshed AuthToken");
//                }
//            }
//            else {
//                Admin admin = adminRepository.findByEmail(userEmail).orElseThrow();
//                Optional<Admin> optionalAdmin = adminRepository.findByEmail(userEmail);
//                if (optionalAdmin.isPresent()) {
//                    if(jwtService.isTokenValid(refreshTokenRequest.getToken(), admin)){
//                        var newToken = jwtService.generateToken(admin);
//
//                        revokeAllUserTokens(customer);
//                        saveUserToken(customer,newToken);
//
//                        res.setStatusCode(200);
//                        res.setToken(newToken);
//                        res.setRefreshToken(refreshTokenRequest.getToken());
//                        res.setExpirationTime("24hr");
//                        res.setMessage("Successfully Refreshed AuthToken");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            res.setStatusCode(500);
//            res.setMessage(e.getMessage());
//        }
//        return res;
//    }
//
//    public void saveUserToken(Customer customer, String newToken) {
//        var token = AuthToken.builder()
//                .users(customer)
//                .token(newToken)
//                .tokenType(TokenType.BEARER)
//                .expired(false)
//                .revoked(false)
//                .build();
//
//        tokenRepository.save(token);
//    }
//
//    public void saveAdminToken(Admin admin, String newToken){
//        var token = AuthToken.builder()
//                .admin(admin)
//                .token(newToken)
//                .tokenType(TokenType.BEARER)
//                .expired(false)
//                .revoked(false)
//                .build();
//
//        tokenRepository.save(token);
//    }
//
//    public void revokeAllUserTokens(Customer customer){
//        var validUserTokens = tokenRepository.findValidTokenByCustomer(customer.getCustomerId());
//        if(validUserTokens.isEmpty())
//            return;
//        validUserTokens.forEach(t -> {
//            t.setExpired(true);
//            t.setRevoked(true);
//        });
//        tokenRepository.saveAll(validUserTokens);
//    }
//
//    public void revokeAllAdminTokens(Admin admin){
//        var validAdminToken = tokenRepository.findValidTokenByAdmin(admin.getAdminId());
//        System.out.println(validAdminToken);
//        if(validAdminToken.isEmpty())
//            return;
//        validAdminToken.forEach(t -> {
//            t.setExpired(true);
//            t.setRevoked(true);
//        });
//        tokenRepository.saveAll(validAdminToken);
//    }
}
