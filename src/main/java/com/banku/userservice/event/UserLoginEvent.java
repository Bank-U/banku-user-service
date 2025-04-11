package com.banku.userservice.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginEvent extends UserEvent {

    private Boolean isSuccessfulLogin;

    public UserLoginEvent(String aggregateId, Boolean isSuccessfulLogin) {
        this.aggregateId = aggregateId;
        this.isSuccessfulLogin = isSuccessfulLogin;
    }
} 