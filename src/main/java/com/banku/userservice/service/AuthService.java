package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.AuthResponse;
import com.banku.userservice.controller.dto.LoginRequest;
import com.banku.userservice.controller.dto.OAuthLoginRequest;
import com.banku.userservice.controller.dto.RegisterRequest;
import com.banku.userservice.exception.InvalidPasswordException;
import com.banku.userservice.exception.UserNotFoundException;
import com.banku.userservice.repository.UserAggregateRepository;
import com.banku.userservice.security.JwtService;
import com.banku.userservice.service.oauth.OAuthProvider;
import com.banku.userservice.service.oauth.OAuthProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAggregateRepository aggregateRepository;
    private final OAuthProviderService oAuthProviderService;
    @Value("${frontend.redirect-url}")
    private String frontendRedirectUrl;


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
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            UserAggregate aggregate = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
            
            aggregateRepository.loginUser(aggregate, false);

            if (aggregate.isDeleted()) {
                throw new UserNotFoundException("User is locked");
            } else {
                throw new UserNotFoundException("User not found"); // Send "User not found" to the frontend to avoid leaking information
            }
        }   
        
        // Get user details to include in token
        UserAggregate aggregate = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Generate token and return response
        String token = generateToken(request.getEmail(), aggregate.getId());

        aggregateRepository.loginUser(aggregate, true);

        return new AuthResponse(token, aggregate.getId());
    }
    
    private String generateToken(String email, String userId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        return jwtService.generateToken(email, extraClaims);
    
    }

    public AuthResponse refresh(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract username from token
        String username = jwtService.extractUsername(token);
        if (username == null) {
            throw new UserNotFoundException("Invalid token");
        }

        // Get user details
        UserAggregate aggregate = userService.findByEmail(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate new token
        String newToken = generateToken(username, aggregate.getId());

        return new AuthResponse(newToken, aggregate.getId());
    }

    public AuthResponse oauth2Login(OAuthLoginRequest request) {
        OAuthProvider provider = oAuthProviderService.getProvider(request.getProvider());
        UserAggregate oauthUser = provider.getUserInfo(request.getCode());

        Optional<UserAggregate> existingUser = userService.findByEmail(oauthUser.getEmail());
        UserAggregate user = existingUser.orElseGet(() -> userService.register(oauthUser));

        String jwtToken = generateToken(user.getEmail(), user.getId());
        return new AuthResponse(jwtToken, user.getId());
    }

    public ResponseEntity<Void> handleOAuth2Callback(String provider, String code) {
        OAuthProvider oAuthProvider = oAuthProviderService.getProvider(provider);
        UserAggregate oauthUser = oAuthProvider.getUserInfo(code);

        Optional<UserAggregate> existingUser = userService.findByEmail(oauthUser.getEmail());
        UserAggregate user = existingUser.orElseGet(() -> userService.register(oauthUser));

        String jwtToken = generateToken(user.getEmail(), user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", frontendRedirectUrl + "?token=" + jwtToken);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
} 