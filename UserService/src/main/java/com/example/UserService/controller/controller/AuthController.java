package com.example.UserService.controller.controller;

import com.example.UserService.controller.configuration.jwt.TokenUtils;
import com.example.UserService.controller.configuration.jwt.UserDetailsImpl;
import com.example.UserService.controller.request.LoginRequest;
import com.example.UserService.controller.request.SignupRequest;
import com.example.UserService.service.mapper.UserMapper;
import com.example.UserService.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication Entrypoint", description = "Open endpoints managing standard signup protocols and credential authentications")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TokenUtils jwtUtils;

    @Operation(
            summary = "Authenticate user",
            description = "Validates secure identity parameters. Generates access tokens mapped to the response tracking header context."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful. Token appended to headers."),
            @ApiResponse(responseCode = "401", description = "Invalid login or security password combination")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword())
        );
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(user.getId(), user.getUsername());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .build();
    }

    @Operation(
            summary = "Register a new user",
            description = "Assembles standard system accounts. Automatically maps baseline metrics and checks constraints for keys like emails and logins."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registration finalized successfully"),
            @ApiResponse(responseCode = "400", description = "Request structural format payload violation"),
            @ApiResponse(responseCode = "409", description = "Identity records matching fields conflict inside storage systems")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid SignupRequest request) {
        userService.registerUser(userMapper.signupRequestToRegisterUserDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}