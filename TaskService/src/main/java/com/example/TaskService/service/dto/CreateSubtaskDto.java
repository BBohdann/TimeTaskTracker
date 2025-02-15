package com.example.TaskService.service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateSubtaskDto {
    private Long taskId;
    private String subtaskName;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime endTime;
    private Integer timeToSpend;
}
