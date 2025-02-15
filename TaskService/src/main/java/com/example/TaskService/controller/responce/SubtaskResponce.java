package com.example.TaskService.controller.responce;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubtaskResponce {
    private Long id;

    private Long taskId;

    private LocalDateTime createdTime;
}
