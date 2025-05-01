package com.banku.userservice.aggregate;

import com.banku.userservice.event.UserCreatedEvent;
import com.banku.userservice.event.UserUpdatedEvent;
import com.banku.userservice.event.UserLoginEvent;
import com.banku.userservice.event.UserDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserAggregateTest {

    private UserAggregate userAggregate;

    @BeforeEach
    void setUp() {
        userAggregate = new UserAggregate();
    }

    @Test
    void testApplyUserCreatedEvent() {
        UserCreatedEvent event = new UserCreatedEvent(
            "test-id",
            "test@example.com",
            "password123",
            "local",
            "123",
            "John",
            "Doe",
            "profile.jpg"
        );
        event.setVersion(1L);

        userAggregate.apply(event);

        assertEquals("test@example.com", userAggregate.getEmail());
        assertEquals("password123", userAggregate.getPassword());
        assertEquals("local", userAggregate.getProvider());
        assertEquals("123", userAggregate.getProviderId());
        assertEquals("John", userAggregate.getFirstName());
        assertEquals("Doe", userAggregate.getLastName());
        assertEquals("profile.jpg", userAggregate.getProfilePicture());
        assertEquals(1L, userAggregate.getVersion());
    }

    @Test
    void testApplyUserUpdatedEvent() {
        // First create a user
        UserCreatedEvent createEvent = new UserCreatedEvent(
            "test-id",
            "test@example.com",
            "password123"
        );
        createEvent.setVersion(1L);
        userAggregate.apply(createEvent);

        // Then update it
        UserUpdatedEvent updateEvent = new UserUpdatedEvent(
            "test-id",
            "new@example.com",
            "newpassword123"
        );
        updateEvent.setVersion(2L);

        userAggregate.apply(updateEvent);

        assertEquals("new@example.com", userAggregate.getEmail());
        assertEquals("newpassword123", userAggregate.getPassword());
        assertEquals(2L, userAggregate.getVersion());
    }

    @Test
    void testApplyUserLoginEvent() {
        UserLoginEvent event = new UserLoginEvent(
            "test-id",
            true
        );
        event.setVersion(1L);

        userAggregate.apply(event);

        assertEquals(1, userAggregate.getLoginHistory().size());
        assertTrue(userAggregate.getLoginHistory().get(0).isSuccessful());
    }

    @Test
    void testApplyUserDeletedEvent() {
        UserDeletedEvent event = new UserDeletedEvent("test-id");
        event.setVersion(1L);

        userAggregate.apply(event);

        assertTrue(userAggregate.isDeleted());
    }

    @Test
    void testUserDetailsMethods() {
        UserCreatedEvent event = new UserCreatedEvent(
            "test-id",
            "test@example.com",
            "password123"
        );
        event.setVersion(1L);
        userAggregate.apply(event);

        assertEquals("test@example.com", userAggregate.getUsername());
        assertTrue(userAggregate.isAccountNonExpired());
        assertTrue(userAggregate.isAccountNonLocked());
        assertTrue(userAggregate.isCredentialsNonExpired());
        assertTrue(userAggregate.isEnabled());

        Collection<? extends GrantedAuthority> authorities = userAggregate.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testUserDetailsMethodsAfterDeletion() {
        UserCreatedEvent createEvent = new UserCreatedEvent(
            "test-id",
            "test@example.com",
            "password123"
        );
        createEvent.setVersion(1L);
        userAggregate.apply(createEvent);

        UserDeletedEvent deleteEvent = new UserDeletedEvent("test-id");
        deleteEvent.setVersion(2L);
        userAggregate.apply(deleteEvent);

        assertFalse(userAggregate.isAccountNonExpired());
        assertFalse(userAggregate.isAccountNonLocked());
        assertFalse(userAggregate.isCredentialsNonExpired());
        assertFalse(userAggregate.isEnabled());
    }
} 