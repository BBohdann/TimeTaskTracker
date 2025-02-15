package com.example.UserService.service.exception;

public class UserAlreadyExistException extends RuntimeException {

    private static final String USER_ALREADY_EXIST_EXCEPTION_TEXT = "User with login = %s already exist.";

    public UserAlreadyExistException(String login) {
        super(String.format(USER_ALREADY_EXIST_EXCEPTION_TEXT, login));
    }
}