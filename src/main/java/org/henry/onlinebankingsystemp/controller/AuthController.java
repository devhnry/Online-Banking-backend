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

    private final AuthenticationService authenticationService;

    @PostMapping("/onboard")
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> signup(@RequestBody OnboardUserDto request){
        DefaultApiResponse<SuccessfulOnboardDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultApiResponse<AuthorisationResponseDto>> login(@RequestBody LoginRequestDTO request){
        DefaultApiResponse<AuthorisationResponseDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<DefaultApiResponse<AuthorisationResponseDto>> refreshToken(@RequestBody RefreshTokenDTO request){
        DefaultApiResponse<AuthorisationResponseDto> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

