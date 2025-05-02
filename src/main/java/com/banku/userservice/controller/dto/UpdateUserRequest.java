package com.banku.userservice.controller.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String currentPassword;
    private String newPassword;
    private String preferredLanguage;
} 