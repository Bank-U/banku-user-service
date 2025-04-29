package com.banku.userservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthLoginRequest {
    @NotBlank(message = "Provider is required")
    private String provider;
    
    @NotBlank(message = "Code is required")
    private String code;
} 