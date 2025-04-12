package com.banku.userservice.controller;

import com.banku.userservice.controller.dto.UserSelfResponse;
import com.banku.userservice.controller.dto.UpdateUserRequest;
import com.banku.userservice.exception.UnauthorizedAccessException;
import com.banku.userservice.security.JwtService;
import com.banku.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/self")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserSelfResponse> getSelf() {
        String userId = JwtService.extractUserId();
        if (userId == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        return ResponseEntity.ok(userService.getSelf(userId));
    }

    @PutMapping
    public ResponseEntity<UserSelfResponse> updateUser(@RequestBody UpdateUserRequest request) {
        String userId = JwtService.extractUserId();
        if (userId == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        String userId = JwtService.extractUserId();
        if (userId == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        log.info("DeletingDeletingDeletingDeletingDeletingDeleting user with ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
