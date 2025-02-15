package com.example.TaskService.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTimeSpentRequest {
    @NotNull
    private Long id;

    @Min(1)
    private Integer timeSpent;
}
