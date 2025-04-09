package com.banku.userservice.service;

import com.banku.userservice.event.UserEvent;
import com.banku.userservice.event.UserCreatedEvent;
import com.banku.userservice.event.UserUpdatedEvent;
import com.banku.userservice.event.UserDeletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonNode jsonNode;

    @InjectMocks
    private KafkaService kafkaService;

    private static final String TEST_AGGREGATE_ID = "test123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TOPIC = "banku.user";

    private UserCreatedEvent testCreatedEvent;
    private UserUpdatedEvent testUpdatedEvent;
    private UserDeletedEvent testDeletedEvent;

    @BeforeEach
    void setUp() {
        testCreatedEvent = new UserCreatedEvent(TEST_AGGREGATE_ID, TEST_EMAIL, TEST_PASSWORD);
        testUpdatedEvent = new UserUpdatedEvent(TEST_AGGREGATE_ID, TEST_EMAIL, TEST_PASSWORD);
        testDeletedEvent = new UserDeletedEvent(TEST_AGGREGATE_ID);
    }

    @Test
    void publishEvent_WhenUserCreatedEvent_ShouldPublishSuccessfully() throws Exception {
        // Arrange
        String serializedEvent = "{\"type\":\"UserCreatedEvent\"}";
        when(objectMapper.writeValueAsString(testCreatedEvent)).thenReturn(serializedEvent);
        when(kafkaTemplate.send(eq(TOPIC), eq(TEST_AGGREGATE_ID), any()))
            .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        // Act
        kafkaService.publishEvent(testCreatedEvent);

        // Assert
        verify(kafkaTemplate).send(TOPIC, TEST_AGGREGATE_ID, serializedEvent);
    }

    @Test
    void publishEvent_WhenUserUpdatedEvent_ShouldPublishSuccessfully() throws Exception {
        // Arrange
        String serializedEvent = "{\"type\":\"UserUpdatedEvent\"}";
        when(objectMapper.writeValueAsString(testUpdatedEvent)).thenReturn(serializedEvent);
        when(kafkaTemplate.send(eq(TOPIC), eq(TEST_AGGREGATE_ID), any()))
            .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        // Act
        kafkaService.publishEvent(testUpdatedEvent);

        // Assert
        verify(kafkaTemplate).send(TOPIC, TEST_AGGREGATE_ID, serializedEvent);
    }

    @Test
    void publishEvent_WhenUserDeletedEvent_ShouldPublishSuccessfully() throws Exception {
        // Arrange
        String serializedEvent = "{\"type\":\"UserDeletedEvent\"}";
        when(objectMapper.writeValueAsString(testDeletedEvent)).thenReturn(serializedEvent);
        when(kafkaTemplate.send(eq(TOPIC), eq(TEST_AGGREGATE_ID), any()))
            .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        // Act
        kafkaService.publishEvent(testDeletedEvent);

        // Assert
        verify(kafkaTemplate).send(TOPIC, TEST_AGGREGATE_ID, serializedEvent);
    }

    @Test
    void publishEvent_WhenSerializationFails_ShouldHandleError() throws Exception {
        // Arrange
        when(objectMapper.writeValueAsString(any(UserEvent.class)))
            .thenThrow(new RuntimeException("Serialization error"));

        // Act & Assert
        try {
            kafkaService.publishEvent(testCreatedEvent);
        } catch (RuntimeException e) {
            verify(kafkaTemplate, never()).send(any(), any(), any());
            return;
        }
        fail("Expected RuntimeException was not thrown");
    }

    @Test
    void listenEvents_WhenValidMessage_ShouldDeserializeAndProcess() throws Exception {
        // Arrange
        String message = "{\"@class\":\"com.banku.userservice.event.UserCreatedEvent\"}";
        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.get("@class")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("com.banku.userservice.event.UserCreatedEvent");
        when(objectMapper.readValue(message, UserCreatedEvent.class)).thenReturn(testCreatedEvent);

        // Act
        kafkaService.listenEvents(message);

        // Assert
        verify(objectMapper).readTree(message);
        verify(objectMapper).readValue(message, UserCreatedEvent.class);
    }

    @Test
    void listenEvents_WhenInvalidMessage_ShouldHandleError() throws Exception {
        // Arrange
        String invalidMessage = "invalid json";
        when(objectMapper.readTree(invalidMessage)).thenThrow(new RuntimeException("Invalid JSON"));

        // Act
        kafkaService.listenEvents(invalidMessage);

        // Assert
        verify(objectMapper).readTree(invalidMessage);
    }
} 