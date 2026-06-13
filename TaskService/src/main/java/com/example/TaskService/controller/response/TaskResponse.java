package com.example.TaskService.controller.response;

import com.example.TaskService.service.dto.SubtaskMainInfoDto;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskResponse {
    private Long id;
    private String taskName;
    private String description;
    private Instant createdTime;
    private Instant endTime;
    private Integer timeToSpend;
    private Integer timeSpent;
    private Boolean isComplete;
    private List<SubtaskMainInfoDto> subtasks;
}
