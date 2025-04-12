package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.UpdateUserRequest;
import com.banku.userservice.controller.dto.UserSelfResponse;
import com.banku.userservice.exception.DuplicateEmailException;
import com.banku.userservice.exception.UserNotFoundException;
import com.banku.userservice.repository.UserAggregateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserAggregateRepository userAggregateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_USER_ID = "user123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";

    private UserAggregate testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserAggregate();
        testUser.setId(TEST_USER_ID);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(ENCODED_PASSWORD);
    }

    @Test
    void register_WhenEmailDoesNotExist_ShouldCreateUser() {
        // Arrange
        when(userAggregateRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        doNothing().when(userAggregateRepository).createUser(any(), any(), any());

        // Act
        UserAggregate result = userService.register(TEST_EMAIL, TEST_PASSWORD);

        // Assert
        assertNotNull(result);
        verify(userAggregateRepository).createUser(any(), eq(TEST_EMAIL), eq(ENCODED_PASSWORD));
    }

    @Test
    void register_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(userAggregateRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> 
            userService.register(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    void getSelf_WhenUserExists_ShouldReturnUserResponse() {
        // Arrange
        when(userAggregateRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // Act
        UserSelfResponse response = userService.getSelf(TEST_EMAIL);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_USER_ID, response.getUserId());
        assertEquals(TEST_EMAIL, response.getEmail());
    }

    @Test
    void getSelf_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userAggregateRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> 
            userService.getSelf(TEST_EMAIL)
        );
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // Arrange
        String newEmail = "new@example.com";
        String newPassword = "newPassword123";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail(newEmail);
        request.setNewPassword(newPassword);

        when(userAggregateRepository.findById(TEST_USER_ID)).thenReturn(testUser);
        when(userAggregateRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");

        // Act
        UserSelfResponse response = userService.updateUser(TEST_USER_ID, request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_USER_ID, response.getUserId());
        assertEquals(newEmail, response.getEmail());
        verify(userAggregateRepository).updateUser(eq(TEST_USER_ID), eq(newEmail), any());
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userAggregateRepository.findById(TEST_USER_ID)).thenReturn(null);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail(TEST_EMAIL);
        request.setNewPassword(TEST_PASSWORD);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> 
            userService.updateUser(TEST_USER_ID, request)
        );
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        when(userAggregateRepository.findById(TEST_USER_ID)).thenReturn(testUser);

        // Act
        userService.deleteUser(TEST_USER_ID);

        // Assert
        verify(userAggregateRepository).deleteUser(TEST_USER_ID);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userAggregateRepository.findById(TEST_USER_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> 
            userService.deleteUser(TEST_USER_ID)
        );
    }
} 