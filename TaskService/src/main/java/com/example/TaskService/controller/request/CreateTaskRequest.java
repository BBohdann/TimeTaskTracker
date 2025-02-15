package com.example.TaskService.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank
    @Size(max = 255)
    private String taskName;

    @Size(max = 2000)
    private String description;

    private LocalDateTime endTime;

    @Min(1)
    private Integer timeToSpend;
}
