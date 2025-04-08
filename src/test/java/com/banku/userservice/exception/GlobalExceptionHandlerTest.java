package com.banku.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleApiException_ShouldReturnCorrectResponse() {
        // Arrange
        String message = "Test error message";
        String error = "Test Error";
        ApiException exception = new ApiException(message, HttpStatus.BAD_REQUEST, error);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleApiException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(error, response.getBody().get("error"));
        assertEquals(message, response.getBody().get("message"));
    }
} 