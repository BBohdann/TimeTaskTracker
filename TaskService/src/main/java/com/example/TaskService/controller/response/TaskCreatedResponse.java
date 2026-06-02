package com.example.TaskService.controller.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreatedResponse {
    private Long id;
    private LocalDateTime createdTime;
}