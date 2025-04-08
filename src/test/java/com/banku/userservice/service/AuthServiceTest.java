package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.AuthResponse;
import com.banku.userservice.controller.dto.LoginRequest;
import com.banku.userservice.controller.dto.RegisterRequest;
import com.banku.userservice.exception.UserNotFoundException;
import com.banku.userservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_USER_ID = "user123";
    private static final String TEST_TOKEN = "test.jwt.token";

    private UserAggregate testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new UserAggregate();
        testUser.setId(TEST_USER_ID);
        testUser.setEmail(TEST_EMAIL);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);
    }

    @Test
    void register_ShouldReturnAuthResponse() {
        // Arrange
        when(userService.register(TEST_EMAIL, TEST_PASSWORD)).thenReturn(testUser);
        when(jwtService.generateToken(eq(TEST_EMAIL), any())).thenReturn(TEST_TOKEN);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        assertEquals(TEST_USER_ID, response.getUserId());
    }

    @Test
    void login_WhenCredentialsAreValid_ShouldReturnAuthResponse() {
        // Arrange
        when(userService.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(eq(TEST_EMAIL), any())).thenReturn(TEST_TOKEN);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        assertEquals(TEST_USER_ID, response.getUserId());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userService.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> 
            authService.login(loginRequest)
        );
    }
} 