package com.ratingsystem.service.in;

import com.ratingsystem.dto.request.AuthenticationRequestDto;
import com.ratingsystem.dto.request.RegisterRequestDto;
import com.ratingsystem.dto.response.AuthenticationResponseDto;

public interface AuthService {
    AuthenticationResponseDto register(RegisterRequestDto request);
    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
    void sendPasswordResetCode(String email);
    void resetPassword(String code, String newPassword);
    boolean isResetCodeValid(String code);
}
