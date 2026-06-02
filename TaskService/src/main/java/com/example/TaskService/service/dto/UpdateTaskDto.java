package com.example.TaskService.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskDto {
    private String taskName;
    private String description;
    private LocalDateTime endTime;
    private Integer timeToSpend;
    private Boolean isComplete;

}
