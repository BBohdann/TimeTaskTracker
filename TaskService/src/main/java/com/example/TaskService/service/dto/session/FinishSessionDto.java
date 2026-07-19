package com.example.TaskService.service.dto.session;

import com.example.TaskService.data.entity.WorkSessionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishSessionDto {
    private Long id;
    private Long taskId;
    private Long subtaskId;
    private Integer duration;
    private WorkSessionStatus status;
}
