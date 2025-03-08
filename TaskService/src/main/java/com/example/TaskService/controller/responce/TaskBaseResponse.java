package com.example.TaskService.controller.responce;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskBaseResponse {
    private Long id;
    private String taskName;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime endTime;
    private Integer timeToSpend;
    private Integer timeSpent;
    private Boolean isComplete;
}