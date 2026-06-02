package com.example.TaskService.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubtaskMainInfoDto {
    private Long id;
    private String  subtaskName;
    private Integer timeSpent;
    private Integer timeToSpent;
    private Boolean isComplete;
    private LocalDateTime endTime;
}
