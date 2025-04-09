package com.banku.userservice.aggregate;

import com.banku.userservice.event.UserEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class Aggregate {
    private String id;
    protected long version;
    protected boolean deleted;

    protected Aggregate() {
        this.id = UUID.randomUUID().toString();
        this.version = 0;
        this.deleted = false;
    }

    public abstract void apply(UserEvent event);
} 