package com.example.TaskService.service.exception;

public class SessionIntervalNotFoundException extends RuntimeException {
    public SessionIntervalNotFoundException() {
        super("WorkSessionInterval is not found");
    }
}
