package com.example.TaskService.controller.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdatedSubtaskResponse {
    private Long subtaskId;
    private Long taskId;
    private String subtaskName;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime endTime;
    private Integer timeToSpend;
    private Integer timeSpent;
    private Boolean isComplete;
}
