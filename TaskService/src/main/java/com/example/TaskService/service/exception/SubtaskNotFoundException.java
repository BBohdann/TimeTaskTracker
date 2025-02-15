package com.example.TaskService.service.exception;

public class SubtaskNotFoundException extends Exception {
    private static final String SUBTASK_NOT_FOUND_EXCEPTION_TEXT = "Task not found with id = %s";

    public SubtaskNotFoundException(String subtaskId) {
        super(String.format(SUBTASK_NOT_FOUND_EXCEPTION_TEXT, subtaskId));
    }
}
