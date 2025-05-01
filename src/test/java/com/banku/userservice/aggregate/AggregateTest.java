package com.banku.userservice.aggregate;

import com.banku.userservice.event.UserEvent;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AggregateTest {

    @Test
    void testDefaultConstructor() {
        // Create a concrete implementation of Aggregate for testing
        class TestAggregate extends Aggregate {
            @Override
            public void apply(UserEvent event) {
                // Empty implementation for testing
            }
        }

        TestAggregate aggregate = new TestAggregate();

        assertNotNull(aggregate.getId());
        assertTrue(UUID.fromString(aggregate.getId()) instanceof UUID);
        assertEquals(0, aggregate.getVersion());
        assertFalse(aggregate.isDeleted());
    }

    @Test
    void testSettersAndGetters() {
        // Create a concrete implementation of Aggregate for testing
        class TestAggregate extends Aggregate {
            @Override
            public void apply(UserEvent event) {
                // Empty implementation for testing
            }
        }

        TestAggregate aggregate = new TestAggregate();

        String newId = UUID.randomUUID().toString();
        aggregate.setId(newId);
        aggregate.setVersion(1L);
        aggregate.setDeleted(true);

        assertEquals(newId, aggregate.getId());
        assertEquals(1L, aggregate.getVersion());
        assertTrue(aggregate.isDeleted());
    }
} 