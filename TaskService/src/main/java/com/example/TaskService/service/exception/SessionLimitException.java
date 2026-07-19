package com.example.TaskService.service.exception;

public class SessionLimitException extends RuntimeException {
    private static final String SESSION_LIMIT_EXCEPTION_TEXT = "The maximum number of active sessions has been exceeded. Allowed limit: %s";

    public SessionLimitException(Integer count) {
        super(String.format(SESSION_LIMIT_EXCEPTION_TEXT, count));
    }
}
