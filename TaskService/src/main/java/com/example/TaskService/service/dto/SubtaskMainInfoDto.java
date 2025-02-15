package com.example.TaskService.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubtaskMainInfoDto {
    private Long Id;

    private String  subtaskName;

    private Integer timeSpent;

    private Boolean isCompleate;


    private LocalDateTime endTime;
}
