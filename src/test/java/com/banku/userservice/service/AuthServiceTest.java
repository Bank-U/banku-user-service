package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.AuthResponse;
import com.banku.userservice.controller.dto.LoginRequest;
import com.banku.userservice.controller.dto.OAuthLoginRequest;
import com.banku.userservice.controller.dto.RegisterRequest;
import com.banku.userservice.exception.UserNotFoundException;
import com.banku.userservice.repository.UserAggregateRepository;
import com.banku.userservice.security.JwtService;
import com.banku.userservice.service.oauth.OAuthProvider;
import com.banku.userservice.service.oauth.OAuthProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserAggregateRepository aggregateRepository;

    @Mock
    private OAuthProviderService oAuthProviderService;

    @InjectMocks
    private AuthService authService;

    private String testEmail;
    private String testPassword;
    private String testUserId;
    private String testToken;
    private UserAggregate testUser;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testPassword = "password123";
        testUserId = "test-user-id";
        testToken = "test-token";
        
        testUser = new UserAggregate();
        testUser.setId(testUserId);
        testUser.setEmail(testEmail);
        testUser.setPassword(testPassword);
    }

    @Test
    void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        when(userService.register(testEmail, testPassword)).thenReturn(testUser);
        when(jwtService.generateToken(anyString(), any())).thenReturn(testToken);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        assertEquals(testUserId, response.getUserId());
    }

    @Test
    void testLogin_Successful() {
        LoginRequest request = new LoginRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyString(), any())).thenReturn(testToken);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        assertEquals(testUserId, response.getUserId());
        verify(aggregateRepository).loginUser(testUser, true);
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Invalid credentials") {});
        when(userService.findByEmail(testEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void testRefresh() {
        String refreshToken = "Bearer " + testToken;
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", testUserId);

        when(jwtService.extractUsername(testToken)).thenReturn(testEmail);
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyString(), any())).thenReturn("new-token");

        AuthResponse response = authService.refresh(refreshToken);

        assertNotNull(response);
        assertEquals("new-token", response.getToken());
        assertEquals(testUserId, response.getUserId());
    }

    @Test
    void testRefresh_InvalidToken() {
        String refreshToken = "Bearer " + testToken;

        when(jwtService.extractUsername(testToken)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            authService.refresh(refreshToken);
        });
    }

    @Test
    void testOAuth2Login() {
        OAuthLoginRequest request = new OAuthLoginRequest();
        request.setProvider("google");
        request.setCode("auth-code");

        OAuthProvider provider = mock(OAuthProvider.class);
        when(oAuthProviderService.getProvider("google")).thenReturn(provider);
        when(provider.getUserInfo("auth-code")).thenReturn(testUser);
        when(userService.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userService.register(testUser)).thenReturn(testUser);
        when(jwtService.generateToken(anyString(), any())).thenReturn(testToken);

        AuthResponse response = authService.oauth2Login(request);

        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        assertEquals(testUserId, response.getUserId());
        verify(aggregateRepository).loginUser(testUser, true);
    }

    @Test
    void testHandleOAuth2Callback() {
        String provider = "google";
        String code = "auth-code";
        String frontendUrl = "http://frontend.com";

        OAuthProvider oAuthProvider = mock(OAuthProvider.class);
        when(oAuthProviderService.getProvider(provider)).thenReturn(oAuthProvider);
        when(oAuthProvider.getUserInfo(code)).thenReturn(testUser);
        when(userService.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userService.register(testUser)).thenReturn(testUser);
        when(jwtService.generateToken(anyString(), any())).thenReturn(testToken);

        // Set the frontend redirect URL through reflection since it's a @Value field
        try {
            var field = AuthService.class.getDeclaredField("frontendRedirectUrl");
            field.setAccessible(true);
            field.set(authService, frontendUrl);
        } catch (Exception e) {
            fail("Failed to set frontendRedirectUrl field");
        }

        ResponseEntity<Void> response = authService.handleOAuth2Callback(provider, code);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(frontendUrl + "?token=" + testToken, response.getHeaders().getFirst("Location"));
        verify(aggregateRepository).loginUser(testUser, true);
    }
} 