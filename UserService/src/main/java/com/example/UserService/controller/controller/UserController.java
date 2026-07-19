package com.example.UserService.controller.controller;

import com.example.UserService.controller.configuration.jwt.TokenUtils;
import com.example.UserService.controller.configuration.mvc.CurrentUserId;
import com.example.UserService.controller.request.*;
import com.example.UserService.controller.responce.UserResponse;
import com.example.UserService.service.dto.UserTokenData;
import com.example.UserService.service.mapper.UserMapper;
import com.example.UserService.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Account Management", description = "Operations for modifying authenticated user profiles (login, password, profile details)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final UserService userService;
    private final TokenUtils tokenUtils;
    private final UserMapper userMapper;

    @Operation(
            summary = "Update user login",
            description = "Updates the system login. Returns a newly generated JWT token in the Authorization header containing the updated identity claims."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successfully changed, new token attached to headers"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload syntax"),
            @ApiResponse(responseCode = "409", description = "The requested login is already claimed by another user")
    })
    @PatchMapping("/login")
    public ResponseEntity<Void> updateUserLogin(
            @RequestBody @Valid UpdateLoginRequest request,
            @CurrentUserId Long userId) {
        UserTokenData updatedUser = userService.updateLogin(userId, request.getNewLogin());
        String token = tokenUtils.generateJwtToken(updatedUser.getId(), updatedUser.getLogin());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    @Operation(
            summary = "Update user password",
            description = "Modifies account password. Validates the integrity of the old password string before writing changes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password successfully modified"),
            @ApiResponse(responseCode = "400", description = "Incorrect legacy credentials or password mismatches")
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> updateUserPassword(
            @RequestBody @Valid UpdatePasswordRequest request,
            @CurrentUserId Long userId) {
        userService.updatePassword(userMapper.updatePasswordRequestToDto(request, userId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user nickname", description = "Changes the user display name inside the workspace client layouts.")
    @ApiResponse(responseCode = "204", description = "Nickname successfully processed")
    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateUserNickname(
            @RequestBody @Valid UpdateNicknameRequest request,
            @CurrentUserId Long userId) {
        userService.updateNickname(userId, request.getNewNickname());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user communication email", description = "Updates target contact emails. Subject to global microservice duplicate safety rules.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Email successfully reassigned"),
            @ApiResponse(responseCode = "409", description = "The provided email is already linked to another entity")
    })
    @PatchMapping("/email")
    public ResponseEntity<Void> updateUserEmail(
            @RequestBody @Valid UpdateEmailRequest request,
            @CurrentUserId Long userId) {
        userService.updateEmail(userId, request.getNewEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Permanently delete account", description = "Destroys user references inside the storage structures. Requires pass confirmation.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Identity successfully wiped"),
            @ApiResponse(responseCode = "400", description = "Authorization verification failed due to bad validation parameters")
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @RequestBody @Valid DeleteUserRequest request,
            @CurrentUserId Long userId) {
        userService.deleteUser(userId, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Fetch current user profile data", description = "Resolves contextual details matching the attached access token information.")
    @ApiResponse(responseCode = "200", description = "Account details successfully returned")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(@CurrentUserId Long userId) {
        UserResponse response = userMapper.userDtoToUserResponse(userService.getUser(userId));
        return ResponseEntity.ok(response);
    }
}