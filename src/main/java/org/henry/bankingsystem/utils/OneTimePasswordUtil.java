package org.henry.bankingsystem.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.henry.bankingsystem.dto.*;
import org.henry.bankingsystem.entity.Account;
import org.henry.bankingsystem.entity.Customer;
import org.henry.bankingsystem.entity.OneTimePassword;
import org.henry.bankingsystem.enums.ContextType;
import org.henry.bankingsystem.enums.VerifyOtpResponse;
import org.henry.bankingsystem.exceptions.ResourceNotFoundException;
import org.henry.bankingsystem.repository.AccountRepository;
import org.henry.bankingsystem.repository.OtpRepository;
import org.henry.bankingsystem.repository.UserRepository;
import org.henry.bankingsystem.service.EmailService;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.henry.bankingsystem.constants.StatusCodeConstants.*;

@Service
@Slf4j @RequiredArgsConstructor
public class OneTimePasswordUtil {
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final AccountRepository accountRepository;

    public DefaultApiResponse<OneTimePasswordDto> sendOtp(String customerEmail, ContextType contextType) {
        DefaultApiResponse<OneTimePasswordDto> response = new DefaultApiResponse<>();
        Customer customer;
        OneTimePassword oneTimePassword;

        try {
            // Fetch customer details from the database
            customer = userRepository.findByEmail(customerEmail).orElseThrow(
                    () -> {
                        log.error("Customer with email {} does not exist", customerEmail);
                        return new ResourceNotFoundException(String.format("Customer with email %s does not exist", customerEmail));
                    });

            // Generates OTP Code and sets Time for Validity
            String otpCode = RandomStringUtils.random(6 , "0123456789");
            long expirationTime = 15;

            log.info("Generating OTP for user {}.", customerEmail);
            oneTimePassword = OneTimePassword.builder()
                    .otpCode(otpCode)
                    .generatedTime(Instant.now())
                    .expirationTime(Instant.now().plus(expirationTime, ChronoUnit.MINUTES))
                    .expiresDuration(String.format("%d Minutes", expirationTime))
                    .customer(customer)
                    .build();
            otpRepository.save(oneTimePassword);


            CustomerDto customerData = CustomerDto.builder()
                    .customerId(customer.getCustomerId())
                    .fullName(String.format("%s %s", customer.getFirstName(), customer.getLastName()))
                    .phoneNumber(customer.getPhoneNumber())
                    .email(customerEmail)
                    .build();

            OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
            oneTimePasswordDto.setOtpCode(otpCode);
            oneTimePasswordDto.setExpirationDuration(oneTimePasswordDto.getExpirationDuration());
            oneTimePasswordDto.setCustomer(customerData);

            response.setStatusCode(OTP_SENT_SUCCESS);
            response.setStatusMessage("Successfully Generated OTP and Sent Email for user " + customerEmail);
            response.setData(oneTimePasswordDto);

            log.info("OTP generated for user {}.", customerEmail);

        } catch (ResourceNotFoundException e) {
            log.error("Customer with email {} does not exist on the DB", customerEmail);
            throw new ResourceNotFoundException("Customer with email " + customerEmail + " does not exist");
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }

        log.info("Sending OTP to customer via Email: {}", customerEmail);

        try{
            Context emailContext = generateEmailContext(customer, oneTimePassword);
            switch (contextType){
                case ContextType.ONBOARDING ->
                {
                    emailService.sendEmail(customer.getEmail().trim(), "Onboarding Process: Verify OTP", emailContext, "verifyOtpTemplate");
                }
                case ContextType.PASSWORD_UPDATE -> {
                    emailService.sendEmail(customer.getEmail().trim(), "Update Password: Verify OTP", emailContext, "passwordChangeTemplate");
                }
            }
        }catch (RuntimeException e){
            log.error("Error Occurred in sending OTP verification email after Three tries");
        }

        log.info("OTP for email {} has been sent successfully.", customerEmail);

        return response;
    }

    public VerifyOtpResponse verifyOtp(String code, String email) {
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        try {
            // Checks for the existing OTP on the DB
            Optional<OneTimePassword> existingOtpOpt = otpRepository.findByOtpCode(code);

            log.info("Checking if One Time Password Exists");
            if (existingOtpOpt.isEmpty()) {
                // If OTP is not found
                log.info("OTP does not exist on the DB");
                return VerifyOtpResponse.NOT_FOUND;
            }

            // Checks if OTP has not reached his expiration time
            log.info("Checking if OTP has reached expiration Time");
            OneTimePassword oneTimePassword = existingOtpOpt.get();
            if (Instant.now().isAfter(oneTimePassword.getExpirationTime())) {
                log.info("OTP has expired");
                return VerifyOtpResponse.EXPIRED;
            }

            log.info("Comparing OTP for Customer and User sending the Request");
            // Gets the Customer related to the OTP and Compare to the One making the request.
            Customer customer = oneTimePassword.getCustomer();
            Customer existingCustomer = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("Customer with email {} cannot be found", email);
                        return new ResourceNotFoundException(String.format("Customer with email %s does not exist", email));
                    });

            if(customer.getCustomerId().equals(existingCustomer.getCustomerId())) {
                if(oneTimePassword.getVerified()) {
                    log.info("OPT has been used and Verified, now invalid");
                    return VerifyOtpResponse.USED;
                }

                oneTimePassword.setVerified(true);
                otpRepository.save(oneTimePassword);
                userRepository.save(existingCustomer);
                log.info("OTP verified");
            } else {
                log.info("Customer provided Invalid OTP");
                return VerifyOtpResponse.INVALID;
            }
            return VerifyOtpResponse.VERIFIED;
        } catch (ResourceNotFoundException e) {
            log.error("Resource Not Found Exception: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Error occurred while trying to verify OTP: {}", e.getMessage());
            throw e;
        }
    }

    // Gets the Customer's Details and takes the OTP and Assign it to the Variable on the Email Template
    private Context generateEmailContext(Customer customer, OneTimePassword otp){
        Context emailContext = new Context();

        Account account = accountRepository.findAccountByCustomer_CustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Customer Not Found: Customer Id: " + customer.getCustomerId())
        );

        emailContext.setVariable("name", customer.getFirstName() + " " + customer.getLastName());
        emailContext.setVariable("duration", otp.getExpiresDuration());
        emailContext.setVariable("otpCode", otp.getOtpCode());

        log.info("Details of OTP and Customer have been applied to Email Context fpr OnBoarding");
        return emailContext;
    }
}