package com.example.TaskService.service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateSubtaskDto {
    private String subtaskName;
    private String description;
    private LocalDateTime endTime;
    private Integer timeToSpend;
}
