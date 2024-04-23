package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.*;
import org.henry.onlinebankingsystemp.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public DefaultResponse signup(@RequestBody SignUpDTO request){
        return authenticationService.signUp(request);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request){
        return authenticationService.login(request);
    }

    @PostMapping("/refreshToken")
    public LoginResponseDTO refreshToken(@RequestBody RefreshTokenDTO request){
        return authenticationService.refreshToken(request);
    }
}

