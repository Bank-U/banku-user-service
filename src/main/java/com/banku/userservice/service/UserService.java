package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.UpdateUserRequest;
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

    public UserSelfResponse getSelf(String email) {
        UserAggregate aggregate = userAggregateRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
                
        if (aggregate.isDeleted()) {
            throw new UserNotFoundException("User not found");
        }
        
        return new UserSelfResponse(aggregate.getId(), aggregate.getEmail());
    }

    public UserSelfResponse updateUser(String userId, UpdateUserRequest request) {
        UserAggregate aggregate = userAggregateRepository.findById(userId);
        if (aggregate == null) {
            throw new UserNotFoundException("User not found");
        }
        
        // Check if the new email is already used by another user
        if (request.getEmail() != null && !request.getEmail().equals(aggregate.getEmail())) {
            userAggregateRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userId)) {
                    throw new DuplicateEmailException("Email already exists");
                }
            });
        }
        
        userAggregateRepository.updateUser(
                userId,
                request.getEmail(),
                request.getNewPassword() != null ? passwordEncoder.encode(request.getNewPassword()) : null
        );
        
        // Return updated user info
        return new UserSelfResponse(
            userId,
            request.getEmail() != null ? request.getEmail() : aggregate.getEmail()
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