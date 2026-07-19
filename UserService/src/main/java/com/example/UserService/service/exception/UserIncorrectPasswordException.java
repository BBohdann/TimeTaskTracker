package com.example.UserService.service.exception;

public class UserIncorrectPasswordException extends RuntimeException {
    private static final String USER_INCORRECT_PASSWORD_EXCEPTION_TEXT = "Incorrect password for user '%s'.";

    public UserIncorrectPasswordException(String identifier) {
        super(String.format(USER_INCORRECT_PASSWORD_EXCEPTION_TEXT, identifier));
    }
}