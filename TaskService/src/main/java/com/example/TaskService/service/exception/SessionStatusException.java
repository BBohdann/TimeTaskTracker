package com.example.TaskService.service.exception;

import com.example.TaskService.data.entity.WorkSessionStatus;

public class SessionStatusException extends RuntimeException {
    private static final String WRONG_SESSION_STATUS_EXCEPTION_TEXT = "Work session status = %s";

    public SessionStatusException(WorkSessionStatus status) {
        super(String.format(WRONG_SESSION_STATUS_EXCEPTION_TEXT, status));
    }
}
