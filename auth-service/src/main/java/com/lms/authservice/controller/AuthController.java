package com.lms.authservice.controller;

import com.lms.authservice.dto.LoginRequest;
import com.lms.authservice.dto.LoginResponse;
import com.lms.authservice.dto.SignUpRequest;
import com.lms.authservice.dto.SignUpResponse;
import com.lms.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public SignUpResponse signup(@RequestBody @Valid SignUpRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}
