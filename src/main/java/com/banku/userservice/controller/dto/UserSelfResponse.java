package com.banku.userservice.controller.dto;

import java.util.List;

import com.banku.userservice.aggregate.LoginHistoryAggregate;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSelfResponse {
    private String userId;
    private String email;
    private List<LoginHistoryAggregate> loginHistory;
} 