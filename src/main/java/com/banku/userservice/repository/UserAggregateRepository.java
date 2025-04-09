package com.banku.userservice.repository;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.event.UserEvent;
import com.banku.userservice.event.UserCreatedEvent;
import com.banku.userservice.event.UserDeletedEvent;
import com.banku.userservice.event.UserUpdatedEvent;
import com.banku.userservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAggregateRepository implements AggregateRepository<UserAggregate, String> {
    private final EventStore eventStore;
    private final KafkaService kafkaService;

    @Override
    public UserAggregate findById(String id) {
        List<UserEvent> events = eventStore.findByAggregateIdOrderByVersionAsc(id);
        if (events.isEmpty()) {
            return null;
        }

        UserAggregate aggregate = new UserAggregate();
        aggregate.setId(id);
        events.forEach(aggregate::apply);
        return aggregate;
    }

    @Override
    public Optional<UserAggregate> findByEmail(String email) {
        List<UserEvent> allEvents = eventStore.findAll();
        return allEvents.stream()
                .filter(event -> {
                    UserAggregate aggregate = findById(event.getAggregateId());
                    return aggregate != null && email.equals(aggregate.getEmail());
                })
                .map(event -> findById(event.getAggregateId()))
                .findFirst();
    }

    public void createUser(String aggregateId, String email, String password) {
        UserCreatedEvent event = new UserCreatedEvent(aggregateId, email, password);
        event.setVersion(1);
        eventStore.save(event);
        kafkaService.publishEvent(event);
    }

    public void updateUser(String id, String email, String password) {
        UserAggregate aggregate = findById(id);
        if (aggregate != null) {
            UserUpdatedEvent event = new UserUpdatedEvent(id, email, password);
            event.setVersion(aggregate.getVersion() + 1);
            eventStore.save(event);
            kafkaService.publishEvent(event);
        }
    }

    public void deleteUser(String id) {
        UserAggregate aggregate = findById(id);
        if (aggregate != null) {
            UserDeletedEvent event = new UserDeletedEvent(id);
            event.setVersion(aggregate.getVersion() + 1);
            eventStore.save(event);
            kafkaService.publishEvent(event);
        }
    }
} 