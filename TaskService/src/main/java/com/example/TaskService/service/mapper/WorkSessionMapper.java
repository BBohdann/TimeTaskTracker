package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.session.StartSessionRequest;
import com.example.TaskService.controller.response.session.FinishSessionResponse;
import com.example.TaskService.controller.response.session.SessionResponse;
import com.example.TaskService.controller.response.session.StartSessionResponse;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.WorkSession;
import com.example.TaskService.service.dto.session.FinishSessionDto;
import com.example.TaskService.service.dto.session.StartSessionDto;
import com.example.TaskService.service.dto.session.WorkSessionDto;
import com.example.TaskService.service.dto.subtask.SubtaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkSessionMapper {
    WorkSession startSessionDtoToSessionEntity(StartSessionDto dto);

    @Mapping(source = "userId", target = "userId")
    StartSessionDto toStartSessionDto(StartSessionRequest request, Long userId);

    WorkSessionDto sessionEntityToSessionDto(WorkSession entity);

    List<WorkSessionDto> sessionEntityToSessionDto(List<WorkSession> entities);

    List<SessionResponse> sessionDtoToSessionResponse(List<WorkSessionDto> dtos);

    StartSessionResponse startSessionDtoToStartSessionResponse(WorkSessionDto dto);

    @Mapping(source = "duration", target = "duration")
    FinishSessionDto sessionEntityToFinishSessionDto(WorkSession entity, Integer duration);

    FinishSessionResponse finishSessionDtoToFinishSessionResponse(FinishSessionDto dto);
}