package com.example.TaskService.controller.response;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class TaskCreatedResponse {
    private Long id;
    private Instant createdTime;
}