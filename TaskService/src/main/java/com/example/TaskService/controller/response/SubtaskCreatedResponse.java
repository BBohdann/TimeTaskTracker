package com.example.TaskService.controller.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubtaskCreatedResponse {
    private Long id;

    private Long taskId;

    private LocalDateTime createdTime;
}
