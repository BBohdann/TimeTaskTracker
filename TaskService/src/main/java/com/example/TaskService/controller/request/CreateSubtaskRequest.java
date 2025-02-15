package com.example.TaskService.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSubtaskRequest {
    @NotNull
    private Long taskId;

    @NotBlank
    @Size(max = 255)
    private String subtaskName;

    @Size(max = 2000)
    private String description;

    private LocalDateTime endTime;

    @Min(1)
    private Integer timeToSpend;
}