package com.example.TaskService.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SubtaskStatusRequest {
    ALL,
    ACTIVE,
    INACTIVE;
}