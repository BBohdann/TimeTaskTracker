package com.example.UserService.service.exception;

public class UserNotFoundException extends RuntimeException {
    private static final String USER_NOT_FOUND_EXCEPTION_TEXT = "User  not found.";

    public UserNotFoundException() {
        super(String.format(USER_NOT_FOUND_EXCEPTION_TEXT));
    }
}