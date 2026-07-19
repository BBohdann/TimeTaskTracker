package com.example.TaskService.controller.request.session;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSessionRequest {
    @NotNull
    private Long taskId;
    private Long subtaskId;
}