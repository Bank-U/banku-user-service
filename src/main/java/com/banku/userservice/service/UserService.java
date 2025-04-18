package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.UpdateUserRequest;
import com.banku.userservice.controller.dto.UserSelfResponse;
import com.banku.userservice.exception.DuplicateEmailException;
import com.banku.userservice.exception.InvalidPasswordException;
import com.banku.userservice.exception.UserNotFoundException;
import com.banku.userservice.repository.UserAggregateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public UserSelfResponse updateUser(String userId, UpdateUserRequest request) {
        log.info("Updating user with request: {}", request.toString());
        UserAggregate aggregate = userAggregateRepository.findById(userId);
        if (aggregate == null || aggregate.isDeleted()) {
            throw new UserNotFoundException("User not found");
        }
        
        // Validate passwords
        validatePassword(request.getCurrentPassword(), request.getNewPassword(), aggregate.getPassword());

        if (request.getNewPassword() != null) {
            aggregate.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        // Update user with current data (this will create a new event)
        userAggregateRepository.updateUser(userId, request.getEmail(), request.getNewPassword());
        
        // Return current user info
        return new UserSelfResponse(
            userId,
            Optional.ofNullable(request.getEmail()).orElse(aggregate.getEmail()),
            aggregate.getLoginHistory()
        );
    }

    private void validatePassword(String currentPassword, String newPassword, String storedPassword) {
        // If either currentPassword or newPassword is provided, both must be provided
        if ((currentPassword != null && newPassword == null) || (currentPassword == null && newPassword != null)) {
            throw new InvalidPasswordException("Both current and new passwords must be provided together");
        }

        // If both passwords are provided, validate them
        if (currentPassword != null && newPassword != null) {
            // Check if current password matches stored password
            if (!passwordEncoder.matches(currentPassword, storedPassword)) {
                throw new InvalidPasswordException("Invalid current password");
            }

            // Check if new password is different from current password
            if (currentPassword.equals(newPassword)) {
                throw new InvalidPasswordException("New password cannot be the same as current password");
            }
        }
    }

    public void deleteUser(String userId) {
        UserAggregate aggregate = userAggregateRepository.findById(userId);
        if (aggregate == null) {
            throw new UserNotFoundException("User not found");
        }
        
        userAggregateRepository.deleteUser(userId);
    }
} 