package org.henry.bankingsystem.service;

import org.henry.bankingsystem.dto.*;

public interface AuthenticationService {
    DefaultApiResponse<SuccessfulOnboardDto> onBoard(OnboardUserDto requestBody);
    DefaultApiResponse<AuthorisationResponseDto> login(LoginRequestDto requestBody);
    DefaultApiResponse<AuthorisationResponseDto> refreshToken(RefreshTokenDto requestBody);
    DefaultApiResponse<OneTimePasswordDto> sendOtp(String email);
    DefaultApiResponse<?> verifyOtp(VerifyOtpRequest requestBody);
}
