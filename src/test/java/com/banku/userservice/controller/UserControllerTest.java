package com.banku.userservice.controller;

import com.banku.userservice.controller.dto.UpdateUserRequest;
import com.banku.userservice.controller.dto.UserSelfResponse;
import com.banku.userservice.exception.UnauthorizedAccessException;
import com.banku.userservice.security.JwtService;
import com.banku.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private String testUserId;
    private UserSelfResponse testUserResponse;
    private UpdateUserRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";
        
        testUserResponse = UserSelfResponse.builder()
            .userId(testUserId)
            .email("test@example.com")
            .build();
        
        testUpdateRequest = new UpdateUserRequest();
        testUpdateRequest.setEmail("new@example.com");
        testUpdateRequest.setCurrentPassword("currentPassword");
        testUpdateRequest.setNewPassword("newPassword");
    }

    @Test
    void testGetSelf_Success() {
        try (MockedStatic<JwtService> jwtService = mockStatic(JwtService.class)) {
            jwtService.when(JwtService::extractUserId).thenReturn(testUserId);
            when(userService.getSelf(testUserId)).thenReturn(testUserResponse);

            ResponseEntity<UserSelfResponse> response = userController.getSelf();

            assertNotNull(response);
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(testUserResponse, response.getBody());
            verify(userService).getSelf(testUserId);
        }
    }

    @Test
    void testGetSelf_Unauthorized() {
        try (MockedStatic<JwtService> jwtService = mockStatic(JwtService.class)) {
            jwtService.when(JwtService::extractUserId).thenReturn(null);

            assertThrows(UnauthorizedAccessException.class, () -> {
                userController.getSelf();
            });

            verify(userService, never()).getSelf(anyString());
        }
    }

    @Test
    void testUpdateUser_Success() {
        try (MockedStatic<JwtService> jwtService = mockStatic(JwtService.class)) {
            jwtService.when(JwtService::extractUserId).thenReturn(testUserId);

            ResponseEntity<Void> response = userController.updateUser(testUpdateRequest);

            assertNotNull(response);
            assertEquals(204, response.getStatusCodeValue());
            verify(userService).updateUser(testUserId, testUpdateRequest);
        }
    }

    @Test
    void testUpdateUser_Unauthorized() {
        try (MockedStatic<JwtService> jwtService = mockStatic(JwtService.class)) {
            jwtService.when(JwtService::extractUserId).thenReturn(null);

            assertThrows(UnauthorizedAccessException.class, () -> {
                userController.updateUser(testUpdateRequest);
            });

            verify(userService, never()).updateUser(anyString(), any(UpdateUserRequest.class));
        }
    }

    @Test
    void testDeleteUser_Success() {
        try (MockedStatic<JwtService> jwtService = mockStatic(JwtService.class)) {
            jwtService.when(JwtService::extractUserId).thenReturn(testUserId);

            ResponseEntity<Void> response = userController.deleteUser();

            assertNotNull(response);
            assertEquals(204, response.getStatusCodeValue());
            verify(userService).deleteUser(testUserId);
        }
    }

    @Test
    void testDeleteUser_Unauthorized() {
        try (MockedStatic<JwtService> jwtService = mockStatic(JwtService.class)) {
            jwtService.when(JwtService::extractUserId).thenReturn(null);

            assertThrows(UnauthorizedAccessException.class, () -> {
                userController.deleteUser();
            });

            verify(userService, never()).deleteUser(anyString());
        }
    }
} 