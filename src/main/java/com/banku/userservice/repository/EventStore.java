package com.banku.userservice.repository;

import com.banku.userservice.event.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventStore extends MongoRepository<Event, String> {
    List<Event> findByAggregateIdOrderByVersionAsc(String aggregateId);
    List<Event> findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(String aggregateId, long version);
} 