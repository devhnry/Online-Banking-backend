package org.henry.bankingsystem.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.bankingsystem.constants.StatusCodeConstants;
import org.henry.bankingsystem.dto.DefaultApiResponse;
import org.henry.bankingsystem.entity.AuthToken;
import org.henry.bankingsystem.repository.TokenRepository;
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
        DefaultApiResponse res = new DefaultApiResponse();
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        if(authHeader == null || authHeader.isBlank()){
            log.error("Blank Authorisation");
            res.setStatusCode(StatusCodeConstants.GENERIC_ERROR);
            res.setStatusMessage("Blank Authorisation");
            return;
        }
        log.info("Performing LogOut Operation");
        jwtToken = authHeader.substring(7);
        AuthToken storedToken = tokenRepository.findByAccessToken(jwtToken).orElse(null);
        if(storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            System.out.println("Successfully Signed out");

            res.setStatusCode(StatusCodeConstants.LOGIN_SUCCESS);
            res.setStatusMessage("Successfully signed out");
        }
    }
}
