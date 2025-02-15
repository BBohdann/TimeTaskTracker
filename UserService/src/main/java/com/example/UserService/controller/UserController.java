package com.example.UserService.controller;

import com.example.UserService.controller.configuration.jwt.TokenUtils;
import com.example.UserService.controller.configuration.jwt.UserDetailsImpl;
import com.example.UserService.controller.request.UpdateLoginRequest;
import com.example.UserService.controller.request.UpdatePasswordRequest;
import com.example.UserService.service.dto.UserInfoDto;
import com.example.UserService.service.exception.UserIncorrectPasswordException;
import com.example.UserService.service.exception.UserNotFoundException;
import com.example.UserService.service.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity updateUserLogin(@RequestBody UpdateLoginRequest updateLoginRequest) throws UserNotFoundException {
        UserInfoDto newInfo = userService.updateLogin(tokenUtils.getLoginFromJwtToken(getCookieToken()),
                updateLoginRequest.getNewLogin());
        UserDetails userDetails = userService.loadUserByUsername(newInfo.getLogin());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

//        response.addCookie(new Cookie("json-token", tokenUtils.generateJwtToken(authentication)));
//        return ResponseEntity.ok().build();
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + tokenUtils.generateJwtToken(authentication))
                .build();
    }

    @PostMapping("/updatePassword")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update user password",
            description = "Update the password of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity updateUserPassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest)
            throws UserNotFoundException, UserIncorrectPasswordException {
        String jwt = getCookieToken();
        userService.updatePassword(tokenUtils.getLoginFromJwtToken(jwt),
                updatePasswordRequest.getOldPassword(),
                updatePasswordRequest.getNewPassword());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .build();
    }

    @PostMapping("/updateNickname")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update user nickname",
            description = "Update the nickname of the authenticated user. Requires a valid JWT token in the Authorization header."
    )
    public ResponseEntity updateUserNickname(@RequestParam String newNickname) throws UserNotFoundException {
        String jwt = getCookieToken();
        userService.updateNickname(tokenUtils.getLoginFromJwtToken(jwt), newNickname);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .build();
    }

    private static String getCookieToken() {
        SecurityContext context = SecurityContextHolder.getContext();
        UserDetailsImpl userDetails = (UserDetailsImpl) context.getAuthentication().getPrincipal();
        return userDetails.getToken();
    }
}