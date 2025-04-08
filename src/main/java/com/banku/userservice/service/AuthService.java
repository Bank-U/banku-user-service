package com.banku.userservice.service;

import com.banku.userservice.controller.dto.AuthResponse;
import com.banku.userservice.controller.dto.LoginRequest;
import com.banku.userservice.controller.dto.RegisterRequest;
import com.banku.userservice.exception.UserNotFoundException;
import com.banku.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        // Register the user
        var user = userService.register(
            request.getEmail(), 
            request.getPassword()
        );
        
        // Generate token and return response
        String token = generateToken(request.getEmail(), user.getId());
        return new AuthResponse(token, user.getId());
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate the user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // Get user details to include in token
        var user = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Generate token and return response
        String token = generateToken(request.getEmail(), user.getId());
        return new AuthResponse(token, user.getId());
    }
    
    private String generateToken(String email, String userId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        return jwtService.generateToken(email, extraClaims);
    }
} 