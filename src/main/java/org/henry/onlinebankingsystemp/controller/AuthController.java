package org.henry.onlinebankingsystemp.controller;

import org.henry.onlinebankingsystemp.dto.RequestResponse;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<RequestResponse> signup(@RequestBody RequestResponse signUpRequest){
        return ResponseEntity.ok(authenticationService.signUp(signUpRequest));
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<RequestResponse> signin(@RequestBody RequestResponse signInRequest){
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<RequestResponse> refreshToken(@RequestBody RequestResponse refreshRequest){
        return ResponseEntity.ok(authenticationService.refreshToken(refreshRequest));
    }
}

