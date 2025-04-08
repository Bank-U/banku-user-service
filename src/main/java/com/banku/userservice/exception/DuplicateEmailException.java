package com.banku.userservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ApiException {
    
    public DuplicateEmailException(String message) {
        super(message, HttpStatus.CONFLICT, "Conflict");
    }
    
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, "Conflict", cause);
    }
} 