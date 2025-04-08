package com.banku.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionTest {

    private static final String TEST_MESSAGE = "Test error message";
    private static final String TEST_ERROR = "Test Error";

    @Test
    void constructor_ShouldSetFields() {
        // Act
        ApiException exception = new ApiException(TEST_MESSAGE, HttpStatus.BAD_REQUEST, TEST_ERROR);

        // Assert
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(TEST_ERROR, exception.getError());
    }

    @Test
    void constructor_WithCause_ShouldSetFields() {
        // Arrange
        Throwable cause = new RuntimeException("Cause");

        // Act
        ApiException exception = new ApiException(TEST_MESSAGE, HttpStatus.BAD_REQUEST, TEST_ERROR, cause);

        // Assert
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(TEST_ERROR, exception.getError());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void toResponse_ShouldReturnCorrectResponse() {
        // Arrange
        ApiException exception = new ApiException(TEST_MESSAGE, HttpStatus.BAD_REQUEST, TEST_ERROR);

        // Act
        ResponseEntity<Map<String, Object>> response = exception.toResponse();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_ERROR, response.getBody().get("error"));
        assertEquals(TEST_MESSAGE, response.getBody().get("message"));
    }
} 