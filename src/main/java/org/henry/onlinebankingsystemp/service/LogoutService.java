package org.henry.onlinebankingsystemp.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.dto.DefaultResponse;
import org.henry.onlinebankingsystemp.repository.TokenRepository;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        DefaultResponse res = new DefaultResponse();
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        if(authHeader == null || authHeader.isBlank()){
            log.error("Blank Authorisation");
            res.setStatusCode(500);
            res.setMessage("Blank Authorisation");
            return;
        }
        log.info("Performing LogOut Operation");
        jwtToken = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwtToken).orElse(null);
        if(storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            System.out.println("Successfully Signed out");

            res.setStatusCode(200);
            res.setMessage("Successfully signed out");
        }
    }
}
