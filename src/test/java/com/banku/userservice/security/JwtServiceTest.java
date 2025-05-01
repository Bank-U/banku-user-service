package com.banku.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private String testSecretKey;
    private String testUsername;
    private String testUserId;
    private Key signingKey;

    @BeforeEach
    void setUp() {
        testSecretKey = "testSecretKey12345678901234567890123456789012";
        testUsername = "test@example.com";
        testUserId = "test-user-id";
        signingKey = Keys.hmacShaKeyFor(testSecretKey.getBytes());
        
        // Set the secret key through reflection since it's a @Value field
        try {
            var field = JwtService.class.getDeclaredField("secretKey");
            field.setAccessible(true);
            field.set(jwtService, testSecretKey);
        } catch (Exception e) {
            fail("Failed to set secretKey field");
        }
    }

    @Test
    void testExtractUsername() {
        String token = generateTestToken(testUsername, testUserId);
        
        String extractedUsername = jwtService.extractUsername(token);
        
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void testExtractUsername_InvalidToken() {
        String invalidToken = "invalid.token";
        
        String extractedUsername = jwtService.extractUsername(invalidToken);
        
        assertNull(extractedUsername);
    }

    @Test
    void testExtractUserId() {
        String token = generateTestToken(testUsername, testUserId);
        
        String extractedUserId = jwtService.extractUserId(token);
        
        assertEquals(testUserId, extractedUserId);
    }

    @Test
    void testExtractUserId_InvalidToken() {
        String invalidToken = "invalid.token";
        
        String extractedUserId = jwtService.extractUserId(invalidToken);
        
        assertNull(extractedUserId);
    }

    @Test
    void testExtractUserId_NoUserIdInToken() {
        String token = generateTestToken(testUsername, null);
        
        String extractedUserId = jwtService.extractUserId(token);
        
        assertEquals(testUsername, extractedUserId);
    }

    @Test
    void testIsTokenValid() {
        String token = generateTestToken(testUsername, testUserId);
        
        boolean isValid = jwtService.isTokenValid(token);
        
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        String expiredToken = generateExpiredToken(testUsername, testUserId);
        
        boolean isValid = jwtService.isTokenValid(expiredToken);
        
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        String invalidToken = "invalid.token";
        
        boolean isValid = jwtService.isTokenValid(invalidToken);
        
        assertFalse(isValid);
    }

    @Test
    void testGenerateToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", testUserId);
        
        String token = jwtService.generateToken(testUsername, extraClaims);
        
        assertNotNull(token);
        assertEquals(testUsername, jwtService.extractUsername(token));
        assertEquals(testUserId, jwtService.extractUserId(token));
    }

    @Test
    void testExtractUserId_FromSecurityContext() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Map<String, Object> details = new HashMap<>();
        details.put("userId", testUserId);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getDetails()).thenReturn(details);
        SecurityContextHolder.setContext(securityContext);
        
        String extractedUserId = JwtService.extractUserId();
        
        assertEquals(testUserId, extractedUserId);
    }

    @Test
    void testExtractUserId_FromSecurityContext_NoAuthentication() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        
        String extractedUserId = JwtService.extractUserId();
        
        assertNull(extractedUserId);
    }

    private String generateTestToken(String username, String userId) {
        Map<String, Object> claims = new HashMap<>();
        if (userId != null) {
            claims.put("userId", userId);
        }
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateExpiredToken(String username, String userId) {
        Map<String, Object> claims = new HashMap<>();
        if (userId != null) {
            claims.put("userId", userId);
        }
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
} 