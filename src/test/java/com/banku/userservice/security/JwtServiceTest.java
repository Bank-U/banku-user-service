package com.banku.userservice.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final String TEST_USERNAME = "test@example.com";
    private static final String TEST_USER_ID = "user123";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", TEST_USER_ID);

        // Act
        String token = jwtService.generateToken(TEST_USERNAME, extraClaims);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_ShouldExtractCorrectUsername() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", TEST_USER_ID);
        String token = jwtService.generateToken(TEST_USERNAME, extraClaims);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void extractClaim_ShouldExtractCorrectClaim() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", TEST_USER_ID);
        String token = jwtService.generateToken(TEST_USERNAME, extraClaims);

        // Act
        String extractedUserId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));

        // Assert
        assertEquals(TEST_USER_ID, extractedUserId);
    }

    @Test
    void isTokenValid_WhenTokenIsValid_ShouldReturnTrue() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", TEST_USER_ID);
        String token = jwtService.generateToken(TEST_USERNAME, extraClaims);

        // Act
        boolean isValid = jwtService.isTokenValid(token, TEST_USERNAME);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WhenUsernameDoesNotMatch_ShouldReturnFalse() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", TEST_USER_ID);
        String token = jwtService.generateToken(TEST_USERNAME, extraClaims);

        // Act
        boolean isValid = jwtService.isTokenValid(token, "different@example.com");

        // Assert
        assertFalse(isValid);
    }
} 