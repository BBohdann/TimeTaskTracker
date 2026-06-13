package com.example.TaskService.service.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class CreateTaskDto {
    private Long userId;
    private String taskName;
    private String description;
    private Instant createdTime;
    private Instant endTime;
    private Integer timeToSpend;
}
