package com.example.TaskService.service.exception;

public class SessionAlreadyExistsException extends RuntimeException {
    public SessionAlreadyExistsException() {
        super("Session is active");
    }
}
