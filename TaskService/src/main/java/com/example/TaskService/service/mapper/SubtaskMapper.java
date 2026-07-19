package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.subtask.CreateSubtaskRequest;
import com.example.TaskService.controller.request.subtask.UpdateSubtaskRequest;
import com.example.TaskService.controller.response.subtask.SubtaskCreatedResponse;
import com.example.TaskService.controller.response.subtask.SubtaskResponse;
import com.example.TaskService.controller.response.subtask.UpdatedSubtaskResponse;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.service.dto.subtask.CreateSubtaskDto;
import com.example.TaskService.service.dto.subtask.SubtaskDto;
import com.example.TaskService.service.dto.subtask.UpdateSubtaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubtaskMapper {
    CreateSubtaskDto subtaskRequestToCreateSubtaskDto(CreateSubtaskRequest taskRequest);

    Subtask createSubtaskDtoToEntity(CreateSubtaskDto dto);

    UpdateSubtaskDto updateSubtaskRequestToUpdateSubtaskDto(UpdateSubtaskRequest request);

    @Mapping(source = "task.id", target = "taskId")
    SubtaskDto subtaskEntityToSubtaskDto(Subtask entity);

    List<SubtaskDto> subtaskEntityToSubtaskDto(List<Subtask> entities);

    UpdatedSubtaskResponse subtaskDtoToUpdatedSubtaskResponse(SubtaskDto dto);

    SubtaskCreatedResponse subtaskDtoToCreatedResponse(SubtaskDto dto);

    SubtaskResponse subtaskDtoToResponse(SubtaskDto dto);

    List<SubtaskResponse> subtaskDtoToSubtaskResponse(List<SubtaskDto> dtos);

    void updateSubtaskFromDto(UpdateSubtaskDto dto, @MappingTarget Subtask subtask);
}