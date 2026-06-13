package com.example.TaskService.controller.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class
UpdateTaskRequest {
    private String taskName;
    private String description;
    private Instant endTime;
    @Min(1)
    private Integer timeToSpend;

    private Boolean isComplete;
}
