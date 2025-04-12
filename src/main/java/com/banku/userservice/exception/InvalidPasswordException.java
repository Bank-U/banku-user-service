package com.banku.userservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ApiException {
    
    public InvalidPasswordException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "Invalid Password");
    }
    
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, "Invalid Password", cause);
    }
}