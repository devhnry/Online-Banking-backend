package org.henry.onlinebankingsystemp.service;

import org.henry.onlinebankingsystemp.dto.*;

public interface AuthenticationService {
    DefaultApiResponse<SuccessfulOnboardDto> onBoard(OnboardUserDto requestBody);
    DefaultApiResponse<AuthorisationResponseDto> login(LoginRequestDto requestBody);
    DefaultApiResponse<AuthorisationResponseDto> refreshToken(RefreshTokenDto requestBody);
    DefaultApiResponse<OneTimePasswordDto> sendOtp(String email);
    DefaultApiResponse<?> verifyOtp(VerifyOtpRequest requestBody);
}
