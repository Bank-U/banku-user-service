package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.UserSelfResponse;
import com.banku.userservice.exception.DuplicateEmailException;
import com.banku.userservice.exception.UserNotFoundException;
import com.banku.userservice.repository.UserAggregateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAggregateRepository userAggregateRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAggregate register(String email, String password) {
        // Check if email already exists
        if (userAggregateRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }
        
        UserAggregate aggregate = new UserAggregate();
        String aggregateId = aggregate.getId();
        
        userAggregateRepository.createUser(
                aggregateId,
                email,
                passwordEncoder.encode(password)
        );
        
        return aggregate;
    }

    public Optional<UserAggregate> findByEmail(String email) {
        return userAggregateRepository.findByEmail(email);
    }

    public UserSelfResponse getSelf(String userId) {
        UserAggregate aggregate = userAggregateRepository.findById(userId);
        if (aggregate == null || aggregate.isDeleted()) {
            throw new UserNotFoundException("User not found");
        }
        
        return new UserSelfResponse(aggregate.getId(), aggregate.getEmail(), aggregate.getLoginHistory());
    }

    public UserSelfResponse updateUser(String userId) {
        UserAggregate aggregate = userAggregateRepository.findById(userId);
        if (aggregate == null || aggregate.isDeleted()) {
            throw new UserNotFoundException("User not found");
        }
        
        // Update user with current data (this will create a new event)
        userAggregateRepository.updateUser(userId, aggregate.getEmail(), null);
        
        // Return current user info
        return new UserSelfResponse(
            userId,
            aggregate.getEmail(),
            aggregate.getLoginHistory()
        );
    }

    public void deleteUser(String userId) {
        UserAggregate aggregate = userAggregateRepository.findById(userId);
        if (aggregate == null) {
            throw new UserNotFoundException("User not found");
        }
        
        userAggregateRepository.deleteUser(userId);
    }
} 