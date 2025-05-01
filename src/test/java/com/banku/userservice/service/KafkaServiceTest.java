package com.banku.userservice.service;

import com.banku.userservice.event.UserCreatedEvent;
import com.banku.userservice.event.UserUpdatedEvent;
import com.banku.userservice.event.UserLoginEvent;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaService kafkaService;

    private String testAggregateId;
    private String testEmail;
    private String testPassword;

    @BeforeEach
    void setUp() {
        testAggregateId = "test-user-id";
        testEmail = "test@example.com";
        testPassword = "password123";
    }

    @Test
    void testPublishUserCreatedEvent() throws Exception {
        UserCreatedEvent event = new UserCreatedEvent(testAggregateId, testEmail, testPassword);
        String jsonMessage = "{\"@class\":\"com.banku.userservice.event.UserCreatedEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        when(objectMapper.writeValueAsString(event)).thenReturn(jsonMessage);
        when(kafkaTemplate.send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage))).thenReturn(future);

        kafkaService.publishEvent(event);

        verify(kafkaTemplate).send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage));
    }

    @Test
    void testPublishUserUpdatedEvent() throws Exception {
        UserUpdatedEvent event = new UserUpdatedEvent(testAggregateId, testEmail, testPassword);
        String jsonMessage = "{\"@class\":\"com.banku.userservice.event.UserUpdatedEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        when(objectMapper.writeValueAsString(event)).thenReturn(jsonMessage);
        when(kafkaTemplate.send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage))).thenReturn(future);

        kafkaService.publishEvent(event);

        verify(kafkaTemplate).send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage));
    }

    @Test
    void testPublishUserLoginEvent() throws Exception {
        UserLoginEvent event = new UserLoginEvent(testAggregateId, true);
        String jsonMessage = "{\"@class\":\"com.banku.userservice.event.UserLoginEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        when(objectMapper.writeValueAsString(event)).thenReturn(jsonMessage);
        when(kafkaTemplate.send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage))).thenReturn(future);

        kafkaService.publishEvent(event);

        verify(kafkaTemplate).send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage));
    }

    @Test
    void testPublishUserDeletedEvent() throws Exception {
        UserDeletedEvent event = new UserDeletedEvent(testAggregateId);
        String jsonMessage = "{\"@class\":\"com.banku.userservice.event.UserDeletedEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        when(objectMapper.writeValueAsString(event)).thenReturn(jsonMessage);
        when(kafkaTemplate.send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage))).thenReturn(future);

        kafkaService.publishEvent(event);

        verify(kafkaTemplate).send(eq("banku.user"), eq(testAggregateId), eq(jsonMessage));
    }

    @Test
    void testListenEvents_UserCreated() throws Exception {
        String message = "{\"@class\":\"com.banku.userservice.event.UserCreatedEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        JsonNode jsonNode = mock(JsonNode.class);
        UserCreatedEvent event = new UserCreatedEvent(testAggregateId, testEmail, testPassword);

        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.get("@class")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("com.banku.userservice.event.UserCreatedEvent");
        when(objectMapper.readValue(message, UserCreatedEvent.class)).thenReturn(event);

        kafkaService.listenEvents(message);

        verify(objectMapper).readTree(message);
        verify(objectMapper).readValue(message, UserCreatedEvent.class);
    }

    @Test
    void testListenEvents_UserUpdated() throws Exception {
        String message = "{\"@class\":\"com.banku.userservice.event.UserUpdatedEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        JsonNode jsonNode = mock(JsonNode.class);
        UserUpdatedEvent event = new UserUpdatedEvent(testAggregateId, testEmail, testPassword);

        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.get("@class")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("com.banku.userservice.event.UserUpdatedEvent");
        when(objectMapper.readValue(message, UserUpdatedEvent.class)).thenReturn(event);

        kafkaService.listenEvents(message);

        verify(objectMapper).readTree(message);
        verify(objectMapper).readValue(message, UserUpdatedEvent.class);
    }

    @Test
    void testListenEvents_UserLogin() throws Exception {
        String message = "{\"@class\":\"com.banku.userservice.event.UserLoginEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        JsonNode jsonNode = mock(JsonNode.class);
        UserLoginEvent event = new UserLoginEvent(testAggregateId, true);

        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.get("@class")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("com.banku.userservice.event.UserLoginEvent");
        when(objectMapper.readValue(message, UserLoginEvent.class)).thenReturn(event);

        kafkaService.listenEvents(message);

        verify(objectMapper).readTree(message);
        verify(objectMapper).readValue(message, UserLoginEvent.class);
    }

    @Test
    void testListenEvents_UserDeleted() throws Exception {
        String message = "{\"@class\":\"com.banku.userservice.event.UserDeletedEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        JsonNode jsonNode = mock(JsonNode.class);
        UserDeletedEvent event = new UserDeletedEvent(testAggregateId);

        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.get("@class")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("com.banku.userservice.event.UserDeletedEvent");
        when(objectMapper.readValue(message, UserDeletedEvent.class)).thenReturn(event);

        kafkaService.listenEvents(message);

        verify(objectMapper).readTree(message);
        verify(objectMapper).readValue(message, UserDeletedEvent.class);
    }

    @Test
    void testListenEvents_UnknownEventType() throws Exception {
        String message = "{\"@class\":\"com.banku.userservice.event.UnknownEvent\",\"aggregateId\":\"" + testAggregateId + "\"}";
        JsonNode jsonNode = mock(JsonNode.class);

        when(objectMapper.readTree(message)).thenReturn(jsonNode);
        when(jsonNode.get("@class")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("com.banku.userservice.event.UnknownEvent");

        kafkaService.listenEvents(message);

        verify(objectMapper).readTree(message);
        verify(objectMapper, never()).readValue(anyString(), any(Class.class));
    }

    @Test
    void testListenEvents_InvalidJson() throws Exception {
        String message = "invalid json";
        
        when(objectMapper.readTree(message)).thenThrow(new RuntimeException("Invalid JSON"));

        kafkaService.listenEvents(message);

        verify(objectMapper).readTree(message);
        verify(objectMapper, never()).readValue(anyString(), any(Class.class));
    }
} 