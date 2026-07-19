package com.example.TaskService.service.dto.task;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class TaskWithSubtasksDto {
    private Long id;
    private String taskName;
    private String description;
    private Instant createdTime;
    private Instant endTime;
    private Integer timeSpent;
    private Integer timeToSpend;
    private Boolean isComplete;

    private List<SubtaskMainInfoDto> subtasks;
}
