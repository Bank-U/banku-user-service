package com.banku.userservice.aggregate;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginHistoryAggregate {
    private Instant loginTime;
    private boolean successful;

    public LoginHistoryAggregate(Instant loginTime, boolean successful) {
        this.loginTime = loginTime;
        this.successful = successful;
    }
}
