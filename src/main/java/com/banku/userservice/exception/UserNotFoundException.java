package com.banku.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "Not Found");
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, "Not Found", cause);
    }
}