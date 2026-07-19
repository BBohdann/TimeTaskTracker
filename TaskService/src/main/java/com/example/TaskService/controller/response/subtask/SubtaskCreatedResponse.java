package com.example.TaskService.controller.response.subtask;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class SubtaskCreatedResponse {
    private Long id;
    private Long taskId;
    private Instant createdTime;
}
