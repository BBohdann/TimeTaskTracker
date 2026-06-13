package com.example.TaskService.service.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class CreateSubtaskDto {
    private String subtaskName;
    private String description;
    private Instant endTime;
    private Integer timeToSpend;
}
