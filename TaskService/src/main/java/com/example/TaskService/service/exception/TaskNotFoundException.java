package com.example.TaskService.service.exception;

public class TaskNotFoundException extends Exception{
    private static final String TASK_NOT_FOUND_EXCEPTION_TEXT = "Task not found with id = %s";

    public TaskNotFoundException(String taskId) {
        super(String.format(TASK_NOT_FOUND_EXCEPTION_TEXT, taskId));
    }
}
