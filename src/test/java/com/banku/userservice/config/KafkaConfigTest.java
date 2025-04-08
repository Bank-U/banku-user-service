package com.banku.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

    @InjectMocks
    private KafkaConfig kafkaConfig;

    @Test
    void userEventsTopic_ShouldCreateTopicWithCorrectConfiguration() {
        // Act
        NewTopic topic = kafkaConfig.userEventsTopic();

        // Assert
        assertNotNull(topic);
        assertEquals("banku.user", topic.name());
        assertEquals(1, topic.numPartitions());
        assertEquals(1, (short) topic.replicationFactor());
    }
} 