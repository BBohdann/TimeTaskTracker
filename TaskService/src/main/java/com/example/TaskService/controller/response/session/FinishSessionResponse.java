package com.example.TaskService.controller.response.session;

import com.example.TaskService.data.entity.WorkSessionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishSessionResponse {
    private Long id;
    private Long taskId;
    private Long subtaskId;
    private Integer duration;
    private WorkSessionStatus status;
}
