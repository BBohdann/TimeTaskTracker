package com.example.TaskService.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TaskStatusRequest {
        ALL,
        ACTIVE,
        INACTIVE;
}
