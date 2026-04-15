package com.lms.authservice.service;

import com.lms.authservice.dto.LoginRequest;
import com.lms.authservice.dto.LoginResponse;
import com.lms.authservice.dto.SignUpRequest;
import com.lms.authservice.dto.SignUpResponse;
import com.lms.authservice.enums.UserRole;
import com.lms.authservice.exception.BadRequestException;
import com.lms.authservice.exception.UnauthorizedException;
import com.lms.authservice.model.User;
import com.lms.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

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
        UserDetails userDetails = userService.loadUserByUsername(savedUser.getEmail());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String accessToken = jwtService.generateAccessToken(auth);
        String refreshToken = jwtService.generateRefreshToken(auth);
        return new SignUpResponse(accessToken, refreshToken);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = jwtService.generateAccessToken(auth);
        String refreshToken = jwtService.generateRefreshToken(auth);
        return new LoginResponse(accessToken, refreshToken);
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = userService.loadUserByUsername(email);

        if (userDetails == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
    }
}
