package com.banku.userservice.aggregate;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LoginHistoryAggregateTest {

    @Test
    void testConstructorAndGetters() {
        Instant loginTime = Instant.now();
        boolean successful = true;

        LoginHistoryAggregate loginHistory = new LoginHistoryAggregate(loginTime, successful);

        assertEquals(loginTime, loginHistory.getLoginTime());
        assertTrue(loginHistory.isSuccessful());
    }

    @Test
    void testSetters() {
        LoginHistoryAggregate loginHistory = new LoginHistoryAggregate(Instant.now(), true);

        Instant newLoginTime = Instant.now().plusSeconds(3600);
        loginHistory.setLoginTime(newLoginTime);
        loginHistory.setSuccessful(false);

        assertEquals(newLoginTime, loginHistory.getLoginTime());
        assertFalse(loginHistory.isSuccessful());
    }
} 