package com.example.TaskService.service.dto.subtask;

import lombok.Data;

import java.time.Instant;

@Data
public class SubtaskDto {
    private Long id;
    private Long taskId;
    private String subtaskName;
    private String description;
    private Instant createdTime;
    private Instant endTime;
    private Integer timeSpent;
    private Integer timeToSpend;
    private Boolean isComplete;
}
