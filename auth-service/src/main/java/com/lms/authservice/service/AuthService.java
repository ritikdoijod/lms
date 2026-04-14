package com.lms.authservice.service;

import com.lms.authservice.dto.LoginRequest;
import com.lms.authservice.dto.LoginResponse;
import com.lms.authservice.dto.SignUpRequest;
import com.lms.authservice.dto.SignUpResponse;
import com.lms.authservice.enums.UserRole;
import com.lms.authservice.exception.BadRequestException;
import com.lms.authservice.model.User;
import com.lms.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse signup(SignUpRequest signUpRequest) {
        User user = userRepository.findByEmail(signUpRequest.getEmail());

        if (user != null) {
            throw new BadRequestException("User already exists with email " + signUpRequest.getEmail());
        }

        User newUser = new User();
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        newUser.setRole(UserRole.USER);
        User savedUser = userRepository.save(newUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        String accessToken = jwtService.generateAccessToken(loginRequest.getEmail());
        String refreshToken = jwtService.generateRefreshToken(loginRequest.getEmail());
        return new LoginResponse(accessToken, refreshToken);
    }
}
