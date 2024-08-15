package org.henry.onlinebankingsystemp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.AuthToken;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.TokenRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.AuthenticationService;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

    private record generateAccessTokenAndRefreshToken(String accessToken, String refreshToken) {}

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
        DefaultApiResponse<AuthorisationResponseDto> response = new DefaultApiResponse<>();
        log.info("Performing Authentication and Processing Login Request");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestBody.email(), requestBody.password()));

            // Checks if the User exists in the System : Prompts to Onboard
            Customer customer = new Customer();
            Optional<Customer> customerOptional = userRepository.findByEmail(requestBody.email());
            if(customerOptional.isPresent()){
                customer = customerOptional.get();
                log.info("User Found on the DB: {}", customer);
            }else{
                log.info("User Not Found on the DB");
                response.setStatusCode(400);
                response.setStatusMessage("Customer Not Found: OnBoard on the System or Verify Email");
            }

            generateAccessTokenAndRefreshToken result = getGenerateAccessTokenAndRefreshToken(customer);

            AuthorisationResponseDto authorisationResponseDto = new AuthorisationResponseDto(
                    result.accessToken(), result.refreshToken(), Instant.now(), "24hrs");

            response.setStatusCode(HttpStatus.OK.value());
            response.setStatusMessage("Successfully Logged In");
            response.setData(authorisationResponseDto);
            log.info("Successfully Logged In: Login ");

        }catch (RuntimeException ex){
            log.error("An error occurred while performing Authentication: {}", ex.getMessage());
        }
        return response;
    }

    @Override
    public DefaultApiResponse<AuthorisationResponseDto> refreshToken(RefreshTokenDto requestBody) {
        log.info("Processing Refreshing Token Request");
        DefaultApiResponse<AuthorisationResponseDto> response = new DefaultApiResponse<>();

        // Gets the User Email of the token that needs to be Refreshed
        String userEmail = jwtService.extractUsername(requestBody.refreshToken());
        log.info("Email of the Refresh Token: {}", userEmail);

        // If Refresh Token has expired.
        log.info("Checking if Refresh token has expired");
        if(jwtService.isTokenExpired(requestBody.refreshToken())){
            response.setStatusCode(200);
            response.setStatusMessage("Refresh Token Expired: User needs to Log in Again");
            log.info("Refresh Token has expired {}", requestBody.refreshToken());
            return response;
        }

        try{
            Optional<Customer> existingCustomer = userRepository.findByEmail(userEmail);
            if(existingCustomer.isPresent()){
                Customer customer = existingCustomer.get();

                log.info("Verifying Token is valid and properly signed");
                // Verifies the token is valid and generates new token for the USER
                if(jwtService.isTokenValid(requestBody.refreshToken(), customer) ){
                    log.info("Generating New Token");

                    String newAccessToken = jwtService.createJWT(customer);

                    revokeOldTokens(customer);
                    saveCustomerToken(customer, newAccessToken, requestBody.refreshToken());

                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setStatusMessage("Successfully Refreshed AuthToken");
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while refreshing Access Token: {}", e.getMessage());
        }
        return response;
    }

    public void saveCustomerToken(Customer customer, String newToken, String refreshToken) {
        AuthToken token = AuthToken.builder()
                .customer(customer)
                .accessToken(newToken)
                .refreshToken(refreshToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeOldTokens(Customer customer) {
        List<AuthToken> validUserTokens = tokenRepository.findValidTokenByCustomer(customer.getCustomerId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
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

    private @NotNull generateAccessTokenAndRefreshToken getGenerateAccessTokenAndRefreshToken(Customer customer) {
        /* Generates AccessToken and RefreshToken for Customer. */
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("customerId", customer.getCustomerId());
        claims.put("email", customer.getEmail());

        String accessToken = jwtService.createJWT(customer);
        String refreshToken = jwtService.generateRefreshToken(claims, customer);

        /* Generates AuthToken for Customer and saves to the DB */
        AuthToken newAuthToken = AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .customer(customer)
                .build();
        tokenRepository.save(newAuthToken);
        return new generateAccessTokenAndRefreshToken(accessToken, refreshToken);
    }
}