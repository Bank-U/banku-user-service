package com.banku.userservice.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdatedEvent extends UserEvent {
    private String email;
    private String password;
    private String preferredLanguage;

    public UserUpdatedEvent(String aggregateId, String email, String password) {
        this.aggregateId = aggregateId;
        this.email = email;
        this.password = password;
    }

    public UserUpdatedEvent(String aggregateId, String email, String password, String preferredLanguage) {
        this.aggregateId = aggregateId;
        this.email = email;
        this.password = password;
        this.preferredLanguage = preferredLanguage;
    }
} 