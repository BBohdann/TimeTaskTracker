package com.example.TaskService.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDtoWithSubtaskInfo {
    private Long id;
    private Long userId;

    @Size(max = 255)
    private String taskName;

    @Size(max = 2000)
    private String description;

    private LocalDateTime createdTime;
    private LocalDateTime endTime;

    @Min(0)
    private Integer timeSpent;

    private Integer timeToSpend;
    private Boolean isComplete;

    private Long subtaskId;

    private String  subtaskName;

    private Long subtaskTimeSpent;

    private Boolean isSubtaskCompleate;
}
