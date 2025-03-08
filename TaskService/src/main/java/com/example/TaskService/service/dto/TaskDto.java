package com.example.TaskService.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDto {
    private Long id;

    @JsonIgnore
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

    private List<SubtaskMainInfoDto> subtasks;
}
