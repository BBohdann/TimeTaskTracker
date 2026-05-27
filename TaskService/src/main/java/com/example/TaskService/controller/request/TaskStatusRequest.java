package com.example.TaskService.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TaskStatusRequest {
        ALL,
        ACTIVE,
        INACTIVE;

    @JsonCreator
    public TaskStatusRequest from(String value) {
        return TaskStatusRequest.valueOf(value.toUpperCase());
    }
}
