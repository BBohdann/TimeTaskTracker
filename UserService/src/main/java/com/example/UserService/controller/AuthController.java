package com.example.UserService.controller;

import com.example.UserService.controller.configuration.jwt.TokenUtils;
import com.example.UserService.controller.configuration.jwt.UserDetailsImpl;
import com.example.UserService.controller.request.LoginRequest;
import com.example.UserService.controller.request.SignupRequest;
import com.example.UserService.service.mapper.UserMapper;
import com.example.UserService.service.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TokenUtils jwtUtils;

//    @Operation(
//            summary = "Authenticate user",
//            description = "Authenticate a user with login and password. Returns a JWT token in the `Authorization` header."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "202", description = "Successfully authenticated, token returned in headers"),
//            @ApiResponse(responseCode = "401", description = "Invalid credentials")
//    })
    @PostMapping("/login")
    public ResponseEntity<Void> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getLogin(),
                                loginRequest.getPassword()
                        )
                );
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(user.getId(), user.getUsername());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .build();
    }

//    @Operation(
//            summary = "Register a new user",
//            description = "Creates a new user account with the provided login, password, email, and nickname."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "202", description = "User successfully registered"),
//            @ApiResponse(responseCode = "400", description = "Invalid request data"),
//            @ApiResponse(responseCode = "409", description = "User with the given login or email already exists")
//    })
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid SignupRequest request) {
        userService.registerUser(userMapper.signupRequestToRegisterUserDto(request));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}