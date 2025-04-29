package com.banku.userservice.controller.dto;

import java.util.List;

import com.banku.userservice.aggregate.LoginHistoryAggregate;
import com.banku.userservice.aggregate.UserAggregate;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSelfResponse {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private List<LoginHistoryAggregate> loginHistory;

    public UserSelfResponse(UserAggregate userAggregate) {
        this.userId = userAggregate.getId();
        this.email = userAggregate.getEmail();
        this.firstName = userAggregate.getFirstName();
        this.lastName = userAggregate.getLastName();
        this.profilePicture = userAggregate.getProfilePicture();
        this.loginHistory = userAggregate.getLoginHistory();
    }
} 