package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.AuthorisationResponseDto;
import org.henry.onlinebankingsystemp.dto.DefaultApiResponse;
import org.henry.onlinebankingsystemp.dto.OnboardUserDto;
import org.henry.onlinebankingsystemp.dto.SuccessfulOnboardDto;
import org.henry.onlinebankingsystemp.dto2.*;
import org.henry.onlinebankingsystemp.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> signup(@RequestBody OnboardUserDto request){
        DefaultApiResponse<SuccessfulOnboardDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint for user login.
     * @param request contains the login credentials.
     * @return a response containing the authorization details (e.g., access token) if login is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<DefaultApiResponse<AuthorisationResponseDto>> login(@RequestBody LoginRequestDTO request){
        DefaultApiResponse<AuthorisationResponseDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint for refreshing the access token using a refresh token.
     * @param request contains the refresh token details.
     * @return a response containing the new authorization details (e.g., new access token).
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<DefaultApiResponse<AuthorisationResponseDto>> refreshToken(@RequestBody RefreshTokenDTO request){
        DefaultApiResponse<AuthorisationResponseDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
