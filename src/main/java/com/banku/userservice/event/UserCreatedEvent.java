package com.banku.userservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent extends UserEvent {
    private String email;
    private String password;
    private String provider;
    private String providerId;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String preferredLanguage;

    public UserCreatedEvent(String aggregateId, String email, String password) {
        this.aggregateId = aggregateId;
        this.email = email;
        this.password = password;
    }

    public UserCreatedEvent(String aggregateId, String email, String password, String provider, String providerId, String firstName, String lastName, String profilePicture, String preferredLanguage) {
        this.aggregateId = aggregateId;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.preferredLanguage = preferredLanguage;
    }
} 