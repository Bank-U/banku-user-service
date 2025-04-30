package com.banku.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * <h3>Kafka configuration for the user service.</h3>
 * <p>
 * This configuration defines the Kafka topics and producer/consumer configurations.
 * It includes:
 * - User events topic
 * </p>
 * <p>
 *  <b>Only applicable for local development.</b>
 * </p>
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name("banku.user")
                .partitions(2)
                .replicas(1)
                .build();
    }
} 