package com.example.TaskService.service.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class UpdateTaskDto {
    private String taskName;
    private String description;
    private Instant endTime;
    private Integer timeToSpend;
    private Boolean isComplete;

}
