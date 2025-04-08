package com.banku.userservice.security;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.repository.UserAggregateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserAggregateRepository userAggregateRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String TEST_EMAIL = "test@example.com";
    private UserAggregate testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserAggregate();
        testUser.setEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        when(userAggregateRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // Act
        var userDetails = authenticationService.loadUserByUsername(TEST_EMAIL);

        // Assert
        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userAggregateRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
            authenticationService.loadUserByUsername(TEST_EMAIL)
        );
    }
} 