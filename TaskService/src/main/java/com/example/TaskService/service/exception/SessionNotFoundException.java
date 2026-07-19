package com.example.TaskService.service.exception;

public class SessionNotFoundException extends RuntimeException {
    private static final String SESSION_NOT_FOUND_EXCEPTION_TEXT = "Session not found with id = %s";

    public SessionNotFoundException(Long sessionId) {
        super(String.format(SESSION_NOT_FOUND_EXCEPTION_TEXT, sessionId));
    }
}
