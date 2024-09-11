package org.henry.bankingsystem.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.bankingsystem.dto.*;
import org.henry.bankingsystem.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    // Service layer dependency to handle authentication-related operations.
    private final AuthenticationService authenticationService;


    /**
     * Endpoint for user onboarding (signup).
     * @param request contains the details required for onboarding a new user.
     * @return a response indicating the success of the onboarding process, including the details of the onboarded user.
     */
    @PostMapping("/onboard")
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> signup(@RequestBody @Validated OnboardUserDto request){
        DefaultApiResponse<SuccessfulOnboardDto> response = authenticationService.onBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint for sending OTP to User's Email (signup).
     * @param email contains the email of the newly onboarded user
     * @return a response indicating the success of the OTP Sent , including the details of the OTP (One Time Password)
     */
    @PostMapping("/send-otp")
    public ResponseEntity<DefaultApiResponse<OneTimePasswordDto>> sendOtp(@RequestParam("email") String email){
        DefaultApiResponse<OneTimePasswordDto> response = authenticationService.sendOtp(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Endpoint for verifying OTP that was sent to the User (signup).
     * @param request contains the details for verifying OTP of onboarded user.
     * @return a response indicating the success of verified OTP or Not.
     */
    @PostMapping("onboard/verify-otp")
    public ResponseEntity<DefaultApiResponse<?>> verifyOtp(@RequestBody @Validated VerifyOtpRequest request){
        DefaultApiResponse<?> response = authenticationService.verifyOtp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint for user login.
     * @param request contains the login credentials.
     * @return a response containing the authorization details (e.g., access authToken) if login is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<DefaultApiResponse<AuthorisationResponseDto>> login(@RequestBody @Validated LoginRequestDto request){
        DefaultApiResponse<AuthorisationResponseDto> response = authenticationService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint for refreshing the access authToken using a refresh authToken.
     * @param request contains the refresh authToken details.
     * @return a response containing the new authorization details (e.g., new access authToken).
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<DefaultApiResponse<AuthorisationResponseDto>> refreshToken(@RequestBody @Validated RefreshTokenDto request){
        DefaultApiResponse<AuthorisationResponseDto> response = authenticationService.refreshToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
