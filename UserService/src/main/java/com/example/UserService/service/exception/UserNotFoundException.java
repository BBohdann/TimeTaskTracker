package com.example.UserService.service.exception;

public class UserNotFoundException extends Exception {

    private static final String USER_NOT_FOUND_EXCEPTION_TEXT = "User with login = %s not found.";

    public UserNotFoundException(String username) {
        super(String.format(USER_NOT_FOUND_EXCEPTION_TEXT, username));
    }
}