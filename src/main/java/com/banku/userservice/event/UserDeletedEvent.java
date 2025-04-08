package com.banku.userservice.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeletedEvent extends Event {
    public UserDeletedEvent(String aggregateId) {
        this.aggregateId = aggregateId;
    }
} 