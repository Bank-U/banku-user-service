package com.banku.userservice.aggregate;

import com.banku.userservice.event.UserEvent;
import com.banku.userservice.event.UserLoginEvent;
import com.banku.userservice.event.UserCreatedEvent;
import com.banku.userservice.event.UserDeletedEvent;
import com.banku.userservice.event.UserUpdatedEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter 
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAggregate extends Aggregate implements UserDetails {
    private String email;
    private String password;
    private String provider;
    private String providerId;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String preferredLanguage;
    @Builder.Default
    private List<LoginHistoryAggregate> loginHistory = new ArrayList<>();

    @Override
    public void apply(UserEvent event) {
        if (event instanceof UserCreatedEvent) {
            apply((UserCreatedEvent) event);
        } else if (event instanceof UserUpdatedEvent) {
            apply((UserUpdatedEvent) event);
        } else if (event instanceof UserDeletedEvent) {
            apply((UserDeletedEvent) event);
        } else if (event instanceof UserLoginEvent) {
            apply((UserLoginEvent) event);
        }
        this.version = event.getVersion();
    }

    private void apply(UserCreatedEvent event) {
        this.email = event.getEmail();
        this.password = event.getPassword();
        this.provider = event.getProvider();
        this.providerId = event.getProviderId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.profilePicture = event.getProfilePicture();
        this.preferredLanguage = event.getPreferredLanguage();
    }

    private void apply(UserUpdatedEvent event) {
        if (event.getEmail() != null) {
            this.email = event.getEmail();
        }
        if (event.getPassword() != null) {
            this.password = event.getPassword();
        }
        if (event.getPreferredLanguage() != null) {
            this.preferredLanguage = event.getPreferredLanguage();
        }
    }

    private void apply(UserLoginEvent event) {
        this.loginHistory.add(new LoginHistoryAggregate(event.getTimestamp(), event.getIsSuccessfulLogin()));
    }

    private void apply(UserDeletedEvent event) {
        this.deleted = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !deleted;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !deleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !deleted;
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }
} 