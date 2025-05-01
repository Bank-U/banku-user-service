package com.banku.userservice.controller;

import com.banku.userservice.controller.dto.AuthResponse;
import com.banku.userservice.controller.dto.LoginRequest;
import com.banku.userservice.controller.dto.OAuthLoginRequest;
import com.banku.userservice.controller.dto.RegisterRequest;
import com.banku.userservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private String testToken;
    private String testUserId;
    private AuthResponse testAuthResponse;
    private RegisterRequest testRegisterRequest;
    private LoginRequest testLoginRequest;
    private OAuthLoginRequest testOAuthLoginRequest;

    @BeforeEach
    void setUp() {
        testToken = "test-token";
        testUserId = "test-user-id";
        
        testAuthResponse = AuthResponse.builder()
            .token(testToken)
            .userId(testUserId)
            .build();
        
        testRegisterRequest = new RegisterRequest();
        testRegisterRequest.setEmail("test@example.com");
        testRegisterRequest.setPassword("password123");
        
        testLoginRequest = new LoginRequest();
        testLoginRequest.setEmail("test@example.com");
        testLoginRequest.setPassword("password123");
        
        testOAuthLoginRequest = new OAuthLoginRequest();
        testOAuthLoginRequest.setProvider("google");
        testOAuthLoginRequest.setCode("auth-code");
    }

    @Test
    void testRegister() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(testAuthResponse);

        ResponseEntity<AuthResponse> response = authController.register(testRegisterRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAuthResponse, response.getBody());
        verify(authService).register(testRegisterRequest);
    }

    @Test
    void testLogin() {
        when(authService.login(any(LoginRequest.class))).thenReturn(testAuthResponse);

        ResponseEntity<AuthResponse> response = authController.login(testLoginRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAuthResponse, response.getBody());
        verify(authService).login(testLoginRequest);
    }

    @Test
    void testRefresh() {
        when(authService.refresh(anyString())).thenReturn(testAuthResponse);

        ResponseEntity<AuthResponse> response = authController.refresh("Bearer " + testToken);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAuthResponse, response.getBody());
        verify(authService).refresh("Bearer " + testToken);
    }

    @Test
    void testOAuth2Login() {
        when(authService.oauth2Login(any(OAuthLoginRequest.class))).thenReturn(testAuthResponse);

        ResponseEntity<AuthResponse> response = authController.oauth2Login(testOAuthLoginRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testAuthResponse, response.getBody());
        verify(authService).oauth2Login(testOAuthLoginRequest);
    }

    @Test
    void testOAuth2Callback() {
        String provider = "google";
        String code = "auth-code";
        ResponseEntity<Void> expectedResponse = ResponseEntity.status(302).build();
        
        when(authService.handleOAuth2Callback(provider, code)).thenReturn(expectedResponse);

        ResponseEntity<Void> response = authController.oauth2Callback(provider, code);

        assertNotNull(response);
        assertEquals(302, response.getStatusCodeValue());
        verify(authService).handleOAuth2Callback(provider, code);
    }
} 