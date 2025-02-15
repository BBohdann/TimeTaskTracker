package com.example.TaskService.service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateTaskDto {
    private Long userId;
    private String taskName;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime endTime;
    private Integer timeToSpend;
    private Integer timeSpent;
}
