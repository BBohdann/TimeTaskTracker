package com.example.UserService.controller;

import com.example.UserService.controller.configuration.jwt.TokenUtils;
import com.example.UserService.controller.request.LoginRequest;
import com.example.UserService.controller.request.SignupRequest;
import com.example.UserService.service.exception.EmailAlreadyExistException;
import com.example.UserService.service.exception.UserAlreadyExistException;
import com.example.UserService.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenUtils jwtUtils;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate a user with login and password. Returns a JWT token in the `Authorization` header."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Successfully authenticated, token returned in headers"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLogin(), loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        Map<String, String> nickname = userService.getUserNickname(authentication.getName());

        return ResponseEntity.accepted()
                .header("Authorization", "Bearer " + jwt)
                .body(nickname);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided login, password, email, and nickname."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "User with the given login or email already exists")
    })
    public ResponseEntity<Void> registerUser(@RequestBody @Valid SignupRequest request)
            throws UserAlreadyExistException, EmailAlreadyExistException {

        userService.registerUser(
                request.getLogin(),
                request.getPassword(),
                request.getEmail(),
                request.getNickname()
        );

        return ResponseEntity.accepted().build();
    }
}