package com.example.UserService.service.exception;

public class EmailAlreadyExistException extends RuntimeException {

    private static final String EMAIL_ALREADY_EXIST_EXCEPTION_TEXT = "User with email = %s already exist.";

    public EmailAlreadyExistException(String email) {
        super(String.format(EMAIL_ALREADY_EXIST_EXCEPTION_TEXT, email));
    }
}
