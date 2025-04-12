package com.banku.userservice.repository;

import com.banku.userservice.event.UserEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventStore extends MongoRepository<UserEvent, String> {
    List<UserEvent> findByAggregateIdOrderByVersionAsc(String aggregateId);
    List<UserEvent> findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(String aggregateId, long version);
} 