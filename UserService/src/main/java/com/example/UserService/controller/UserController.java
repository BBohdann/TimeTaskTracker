package com.example.UserService.controller;

import com.example.UserService.controller.configuration.jwt.TokenUtils;
import com.example.UserService.controller.configuration.mvc.CurrentUserId;
import com.example.UserService.controller.request.*;
import com.example.UserService.controller.responce.UserResponse;
import com.example.UserService.service.dto.UserTokenData;
import com.example.UserService.service.mapper.UserMapper;
import com.example.UserService.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing user information")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final UserService userService;
    private final TokenUtils tokenUtils;
    private final UserMapper userMapper;

    @PatchMapping("/login")
    @Operation(
            summary = "Update user login",
            description = "Update the login of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity<Void> updateUserLogin(
            @RequestBody @Valid UpdateLoginRequest request,
            @CurrentUserId Long userId) {
        UserTokenData updatedUser =
                userService.updateLogin(
                        userId,
                        request.getNewLogin()
                );

        String token = tokenUtils.generateJwtToken(
                updatedUser.getId(),
                updatedUser.getLogin()
        );

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    @PatchMapping("/password")
    @Operation(
            summary = "Update user password",
            description = "Update the password of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity<Void> updateUserPassword(
            @RequestBody @Valid UpdatePasswordRequest request,
            @CurrentUserId Long userId) {
        userService.updatePassword(
                userMapper.updatePasswordRequestToDto(
                        request,
                        userId)
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/nickname")
    @Operation(
            summary = "Update user nickname",
            description = "Update the nickname of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity<Void> updateUserNickname(
            @RequestBody @Valid UpdateNicknameRequest request,
            @CurrentUserId Long userId) {
        userService.updateNickname(
                userId,
                request.getNewNickname()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> updateUserEmail(
            @RequestBody @Valid UpdateEmailRequest request,
            @CurrentUserId Long userId) {
        userService.updateEmail(
                userId,
                request.getNewEmail()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUser(
            @RequestBody @Valid DeleteUserRequest request,
            @CurrentUserId Long userId) {
        userService.deleteUser(
                userId,
                request.getPassword()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(
            @CurrentUserId Long userId) {
        UserResponse response = userMapper.userDtoToUserResponse(
                userService.getUser(userId)
        );

        return ResponseEntity.ok(response);
    }
}