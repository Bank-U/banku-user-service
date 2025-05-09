package com.banku.userservice.controller;

import com.banku.userservice.controller.dto.AuthResponse;
import com.banku.userservice.controller.dto.LoginRequest;
import com.banku.userservice.controller.dto.OAuthLoginRequest;
import com.banku.userservice.controller.dto.RegisterRequest;
import com.banku.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and returns an authentication token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered successfully", 
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates a user and returns an authentication token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User logged in successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "User is locked")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Refresh authentication token",
        description = "Refreshes the authentication token for the current user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.refresh(token));
    }

    @Operation(
        summary = "Login with OAuth2 provider",
        description = "Authenticates a user using OAuth2 provider (Google, Apple, Facebook) and returns an authentication token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User logged in successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "User is locked")
    })
    @PostMapping("/oauth2/login")
    public ResponseEntity<AuthResponse> oauth2Login(@RequestBody OAuthLoginRequest request) {
        return ResponseEntity.ok(authService.oauth2Login(request));
    }
    
    @Operation(
        summary = "OAuth2 callback",
        description = "Handles the OAuth2 callback from the provider"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Redirect to frontend with token"),
        @ApiResponse(responseCode = "400", description = "Invalid callback")
    })
    @GetMapping("/oauth2/callback/{provider}")
    public ResponseEntity<Void> oauth2Callback(
            @PathVariable String provider,
            @RequestParam String code) {
        return authService.handleOAuth2Callback(provider, code);
    }
}
