package com.lms.authservice.service;

import com.lms.authservice.dto.LoginRequestDTO;
import com.lms.authservice.dto.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String accessToken = jwtService.generateAccessToken(loginRequestDTO.getEmail());
        String refreshToken = jwtService.generateRefreshToken(loginRequestDTO.getEmail());
        return new LoginResponseDTO(accessToken, refreshToken);
    }
}
