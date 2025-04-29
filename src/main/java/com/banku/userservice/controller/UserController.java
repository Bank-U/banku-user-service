package com.banku.userservice.controller;

import com.banku.userservice.controller.dto.UserSelfResponse;
import com.banku.userservice.controller.dto.UpdateUserRequest;
import com.banku.userservice.exception.UnauthorizedAccessException;
import com.banku.userservice.security.JwtService;
import com.banku.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/self")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Get user information",
        description = "Retrieves the current user's information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserSelfResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping
    public ResponseEntity<UserSelfResponse> getSelf() {
        String userId = JwtService.extractUserId();
        if (userId == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        return ResponseEntity.ok(userService.getSelf(userId));
    }

    @Operation(
        summary = "Update user information",
        description = "Updates the current user's information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User information updated successfully",
            content = @Content(schema = @Schema(implementation = UserSelfResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PutMapping
    public ResponseEntity<UserSelfResponse> updateUser(@RequestBody UpdateUserRequest request) {
        String userId = JwtService.extractUserId();
        if (userId == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @Operation(
        summary = "Delete user",
        description = "Deletes the current user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
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
