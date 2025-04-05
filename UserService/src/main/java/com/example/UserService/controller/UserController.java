package com.example.UserService.controller;

import com.example.UserService.controller.configuration.jwt.TokenUtils;
import com.example.UserService.controller.configuration.jwt.UserDetailsImpl;
import com.example.UserService.controller.request.UpdateLoginRequest;
import com.example.UserService.controller.request.UpdatePasswordRequest;
import com.example.UserService.service.dto.UserInfoDto;
import com.example.UserService.service.exception.UserIncorrectPasswordException;
import com.example.UserService.service.exception.UserNotFoundException;
import com.example.UserService.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("api/user/")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing user information")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;
    private final TokenUtils tokenUtils;

    @PostMapping("/updateLogin")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update user login",
            description = "Update the login of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity<Void> updateUserLogin(@RequestBody @Valid UpdateLoginRequest request)
            throws UserNotFoundException {

        String currentLogin = tokenUtils.getLoginFromJwtToken(getCookieToken());
        UserInfoDto updatedUser = userService.updateLogin(currentLogin, request.getNewLogin());

        UserDetails userDetails = userService.loadUserByUsername(updatedUser.getLogin());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String newToken = tokenUtils.generateJwtToken(auth);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + newToken)
                .build();
    }

    @PostMapping("/updatePassword")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update user password",
            description = "Update the password of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity<Void> updateUserPassword(@RequestBody @Valid UpdatePasswordRequest request)
            throws UserNotFoundException, UserIncorrectPasswordException {

        String login = tokenUtils.getLoginFromJwtToken(getCookieToken());

        userService.updatePassword(login, request.getOldPassword(), request.getNewPassword());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + getCookieToken())
                .build();
    }

    @PostMapping("/updateNickname")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update user nickname",
            description = "Update the nickname of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity<Void> updateUserNickname(@RequestParam String newNickname)
            throws UserNotFoundException {

        String login = tokenUtils.getLoginFromJwtToken(getCookieToken());

        userService.updateNickname(login, newNickname);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + getCookieToken())
                .build();
    }

    private static String getCookieToken() {
        return ((UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getToken();
    }
}