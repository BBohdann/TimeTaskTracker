package com.example.TaskService.service.dto.task;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class SubtaskMainInfoDto {
    private Long id;
    private String  subtaskName;
    private Integer timeSpent;
    private Integer timeToSpend;
    private Boolean isComplete;
    private Instant endTime;
}
