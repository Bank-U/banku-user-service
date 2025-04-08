package com.banku.userservice.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreatedEvent extends Event {
    private String email;
    private String password;

    public UserCreatedEvent(String aggregateId, String email, String password) {
        this.aggregateId = aggregateId;
        this.email = email;
        this.password = password;
    }
} 