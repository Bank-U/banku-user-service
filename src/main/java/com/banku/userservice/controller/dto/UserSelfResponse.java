package com.banku.userservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSelfResponse {
    private String userId;
    private String email;
} 