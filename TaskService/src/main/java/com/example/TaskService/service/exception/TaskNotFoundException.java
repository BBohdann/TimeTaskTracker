package com.example.TaskService.service.exception;

public class TaskNotFoundException extends RuntimeException{
    private static final String TASK_NOT_FOUND_EXCEPTION_TEXT = "Task not found with id = %s";

    public TaskNotFoundException(Long taskId) {
        super(String.format(TASK_NOT_FOUND_EXCEPTION_TEXT, taskId));
    }
}
