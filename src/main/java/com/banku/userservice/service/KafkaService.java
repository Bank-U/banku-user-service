package com.banku.userservice.service;

import com.banku.userservice.event.UserEvent;
import com.banku.userservice.event.UserCreatedEvent;
import com.banku.userservice.event.UserUpdatedEvent;
import com.banku.userservice.event.UserDeletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishEvent(UserEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("banku.user", event.getAggregateId(), message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Event published successfully: {}", event.getEventType());
                        } else {
                            log.error("Failed to publish event: {}", event.getEventType(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error serializing event: {}", event.getEventType(), e);
            throw new RuntimeException("Error publishing event", e);
        }
    }

    @KafkaListener(topics = "banku.user", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvents(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String className = jsonNode.get("@class").asText();
            
            UserEvent event;
            switch (className) {
                case "com.banku.userservice.event.UserCreatedEvent":
                    event = objectMapper.readValue(message, UserCreatedEvent.class);
                    break;
                case "com.banku.userservice.event.UserUpdatedEvent":
                    event = objectMapper.readValue(message, UserUpdatedEvent.class);
                    break;
                case "com.banku.userservice.event.UserDeletedEvent":
                    event = objectMapper.readValue(message, UserDeletedEvent.class);
                    break;
                default:
                    log.error("Unknown event type: {}", className);
                    return;
            }
            
            log.info("Received event: {} for aggregate: {}", event.getEventType(), event.getAggregateId());
            processEvent(event);
        } catch (Exception e) {
            log.error("Error processing event message: {}", message, e);
        }
    }

    private void processEvent(UserEvent event) {
        if (event instanceof UserCreatedEvent) {
            handleUserCreated((UserCreatedEvent) event);
        } else if (event instanceof UserUpdatedEvent) {
            handleUserUpdated((UserUpdatedEvent) event);
        } else if (event instanceof UserDeletedEvent) {
            handleUserDeleted((UserDeletedEvent) event);
        }
    }

    private void handleUserCreated(UserCreatedEvent event) {
        log.info("Processing user created event for user: {}", event.getAggregateId());
    }

    private void handleUserUpdated(UserUpdatedEvent event) {
        log.info("Processing user updated event for user: {}", event.getAggregateId());
    }

    private void handleUserDeleted(UserDeletedEvent event) {
        log.info("Processing user deleted event for user: {}", event.getAggregateId());
    }
} 