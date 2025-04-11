package com.banku.userservice.event;

import com.banku.userservice.security.JwtService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@Document(collection = "user_events")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class UserEvent {
    @Id
    private String id;
    protected String aggregateId;
    private String eventType;
    private Instant timestamp;
    private long version;
    private String createdBy;

    protected UserEvent() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.eventType = this.getClass().getSimpleName();
        this.createdBy = JwtService.extractUserId();
    }
} 