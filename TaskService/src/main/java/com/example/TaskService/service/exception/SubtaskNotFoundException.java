package com.example.TaskService.service.exception;

public class SubtaskNotFoundException extends RuntimeException {
    private static final String SUBTASK_NOT_FOUND_EXCEPTION_TEXT = "Subtask not found with id = %s";

    public SubtaskNotFoundException(Long subtaskId) {
        super(String.format(SUBTASK_NOT_FOUND_EXCEPTION_TEXT, subtaskId));
    }
}
