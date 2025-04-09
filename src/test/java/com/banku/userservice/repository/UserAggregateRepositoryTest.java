package com.banku.userservice.repository;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.event.UserEvent;
import com.banku.userservice.event.UserCreatedEvent;
import com.banku.userservice.event.UserDeletedEvent;
import com.banku.userservice.event.UserUpdatedEvent;
import com.banku.userservice.service.KafkaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAggregateRepositoryTest {

    @Mock
    private EventStore eventStore;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private UserAggregateRepository repository;

    @Captor
    private ArgumentCaptor<UserEvent> eventCaptor;

    private static final String TEST_ID = "test123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";

    private UserCreatedEvent testCreatedEvent;
    private UserUpdatedEvent testUpdatedEvent;
    private UserDeletedEvent testDeletedEvent;

    @BeforeEach
    void setUp() {
        testCreatedEvent = new UserCreatedEvent(TEST_ID, TEST_EMAIL, TEST_PASSWORD);
        testUpdatedEvent = new UserUpdatedEvent(TEST_ID, TEST_EMAIL, TEST_PASSWORD);
        testDeletedEvent = new UserDeletedEvent(TEST_ID);
    }

    @Test
    void findById_WhenEventsExist_ShouldReturnAggregate() {
        // Arrange
        when(eventStore.findByAggregateIdOrderByVersionAsc(TEST_ID))
            .thenReturn(Arrays.asList(testCreatedEvent));

        // Act
        UserAggregate result = repository.findById(TEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_PASSWORD, result.getPassword());
    }

    @Test
    void findById_WhenNoEvents_ShouldReturnNull() {
        // Arrange
        when(eventStore.findByAggregateIdOrderByVersionAsc(TEST_ID))
            .thenReturn(Collections.emptyList());

        // Act
        UserAggregate result = repository.findById(TEST_ID);

        // Assert
        assertNull(result);
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(eventStore.findAll()).thenReturn(Arrays.asList(testCreatedEvent));
        when(eventStore.findByAggregateIdOrderByVersionAsc(TEST_ID))
            .thenReturn(Arrays.asList(testCreatedEvent));

        // Act
        Optional<UserAggregate> result = repository.findByEmail(TEST_EMAIL);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get().getEmail());
    }

    @Test
    void createUser_ShouldSaveEventAndPublishToKafka() {
        // Act
        repository.createUser(TEST_ID, TEST_EMAIL, TEST_PASSWORD);

        // Assert
        verify(eventStore).save(eventCaptor.capture());
        verify(kafkaService).publishEvent(eventCaptor.getValue());
        
        UserEvent capturedEvent = eventCaptor.getValue();
        assertTrue(capturedEvent instanceof UserCreatedEvent);
        assertEquals(TEST_ID, capturedEvent.getAggregateId());
        assertEquals(1, capturedEvent.getVersion());
    }

    @Test
    void updateUser_WhenUserExists_ShouldSaveEventAndPublishToKafka() {
        // Arrange
        when(eventStore.findByAggregateIdOrderByVersionAsc(TEST_ID))
            .thenReturn(Arrays.asList(testCreatedEvent));

        // Act
        repository.updateUser(TEST_ID, TEST_EMAIL, TEST_PASSWORD);

        // Assert
        verify(eventStore).save(eventCaptor.capture());
        verify(kafkaService).publishEvent(eventCaptor.getValue());
        
        UserEvent capturedEvent = eventCaptor.getValue();
        assertTrue(capturedEvent instanceof UserUpdatedEvent);
        assertEquals(TEST_ID, capturedEvent.getAggregateId());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldSaveEventAndPublishToKafka() {
        // Arrange
        when(eventStore.findByAggregateIdOrderByVersionAsc(TEST_ID))
            .thenReturn(Arrays.asList(testCreatedEvent));

        // Act
        repository.deleteUser(TEST_ID);

        // Assert
        verify(eventStore).save(eventCaptor.capture());
        verify(kafkaService).publishEvent(eventCaptor.getValue());
        
        UserEvent capturedEvent = eventCaptor.getValue();
        assertTrue(capturedEvent instanceof UserDeletedEvent);
        assertEquals(TEST_ID, capturedEvent.getAggregateId());
    }
} 