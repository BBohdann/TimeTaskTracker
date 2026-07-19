package com.example.TaskService.service.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String taskName;
    private String description;
    private Instant createdTime;
    private Instant endTime;
    private Integer timeSpent;
    private Integer timeToSpend;
    private Boolean isComplete;
}
