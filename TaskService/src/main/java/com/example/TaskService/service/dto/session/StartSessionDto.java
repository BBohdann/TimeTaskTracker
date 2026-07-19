package com.example.TaskService.service.dto.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartSessionDto {
    private Long userId;
    private Long taskId;
    private Long subtaskId;
}
