package com.example.TaskService.service.dto.subtask;

import lombok.Data;

import java.time.Instant;

@Data
public class UpdateSubtaskDto {
    private String subtaskName;
    private String description;
    private Instant endTime;
    private Integer timeToSpend;
    private Boolean isComplete;
}