package com.banku.userservice.service;

import com.banku.userservice.aggregate.UserAggregate;
import com.banku.userservice.controller.dto.UpdateUserRequest;
import com.banku.userservice.controller.dto.UserSelfResponse;
import com.banku.userservice.exception.DuplicateEmailException;
import com.banku.userservice.exception.InvalidPasswordException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserAggregateRepository userAggregateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserAggregate testUser;
    private String testUserId;
    private String testEmail;
    private String testPassword;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";
        testEmail = "test@example.com";
        testPassword = "password123";
        
        testUser = new UserAggregate();
        testUser.setId(testUserId);
        testUser.setEmail(testEmail);
        testUser.setPassword(testPassword);
    }

    @Test
    void testRegisterWithEmailAndPassword() {
        when(userAggregateRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn("encodedPassword");
        
        UserAggregate result = userService.register(testEmail, testPassword);
        
        assertNotNull(result);
        verify(userAggregateRepository).createUser(
            anyString(),
            eq(testEmail),
            eq("encodedPassword")
        );
    }

    @Test
    void testRegisterWithEmailAndPassword_DuplicateEmail() {
        when(userAggregateRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        
        assertThrows(DuplicateEmailException.class, () -> {
            userService.register(testEmail, testPassword);
        });
    }

    @Test
    void testRegisterWithUserAggregate() {
        when(userAggregateRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn("encodedPassword");
        
        UserAggregate result = userService.register(testUser);
        
        assertNotNull(result);
        verify(userAggregateRepository).createUser(
            eq(testUserId),
            eq(testEmail),
            eq("encodedPassword"),
            eq(testUser.getProvider()),
            eq(testUser.getProviderId()),
            eq(testUser.getFirstName()),
            eq(testUser.getLastName()),
            eq(testUser.getProfilePicture())
        );
    }

    @Test
    void testFindByEmail() {
        when(userAggregateRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        
        Optional<UserAggregate> result = userService.findByEmail(testEmail);
        
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetSelf() {
        when(userAggregateRepository.findById(testUserId)).thenReturn(testUser);
        
        UserSelfResponse result = userService.getSelf(testUserId);
        
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals(testEmail, result.getEmail());
    }

    @Test
    void testGetSelf_UserNotFound() {
        when(userAggregateRepository.findById(testUserId)).thenReturn(null);
        
        assertThrows(UserNotFoundException.class, () -> {
            userService.getSelf(testUserId);
        });
    }

    @Test
    void testUpdateUser() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@example.com");
        request.setCurrentPassword(testPassword);
        request.setNewPassword("newPassword123");
        
        when(userAggregateRepository.findById(testUserId)).thenReturn(testUser);
        when(passwordEncoder.matches(testPassword, testPassword)).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        
        UserSelfResponse result = userService.updateUser(testUserId, request);
        
        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        verify(userAggregateRepository).updateUser(
            eq(testUserId),
            eq("new@example.com"),
            eq("encodedNewPassword")
        );
    }

    @Test
    void testUpdateUser_InvalidPassword() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123");
        
        when(userAggregateRepository.findById(testUserId)).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPassword", testPassword)).thenReturn(false);
        
        assertThrows(InvalidPasswordException.class, () -> {
            userService.updateUser(testUserId, request);
        });
    }

    @Test
    void testDeleteUser() {
        when(userAggregateRepository.findById(testUserId)).thenReturn(testUser);
        
        userService.deleteUser(testUserId);
        
        verify(userAggregateRepository).deleteUser(testUserId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userAggregateRepository.findById(testUserId)).thenReturn(null);
        
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(testUserId);
        });
    }
} 