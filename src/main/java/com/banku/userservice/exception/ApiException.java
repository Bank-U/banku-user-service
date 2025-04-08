package com.banku.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ApiException extends RuntimeException {
    
    private final HttpStatus httpStatus;
    private final String error;
    
    public ApiException(String message, HttpStatus httpStatus, String error) {
        super(message);
        this.httpStatus = httpStatus;
        this.error = error;
    }
    
    public ApiException(String message, HttpStatus httpStatus, String error, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.error = error;
    }
    
    public ResponseEntity<Map<String, Object>> toResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message", getMessage());
        return ResponseEntity.status(httpStatus).body(response);
    }
} 