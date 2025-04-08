package com.banku.userservice.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "user_events")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class Event {
    @Id
    private String id;
    protected String aggregateId;
    private String eventType;
    private Instant timestamp;
    private long version;

    protected Event() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.eventType = this.getClass().getSimpleName();
    }
} 