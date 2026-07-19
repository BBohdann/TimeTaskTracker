package com.example.TaskService.controller.response.session;

import com.example.TaskService.data.entity.WorkSessionStatus;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SessionResponse {
    private Long id;
    private Long taskId;
    private Long subtaskId;
    private WorkSessionStatus status;
}
