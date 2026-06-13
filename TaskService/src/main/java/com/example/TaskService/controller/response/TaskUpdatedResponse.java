package com.example.TaskService.controller.response;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class TaskUpdatedResponse {
    private Long id;
    private String taskName;
    private String description;
    private Instant createdTime;
    private Instant endTime;
    private Integer timeToSpend;
    private Integer timeSpent;
    private Boolean isComplete;
}
