package com.banku.userservice.repository;

import java.util.Optional;

public interface AggregateRepository<T, ID> {
    T findById(ID id);
    Optional<T> findByEmail(String email);
} 