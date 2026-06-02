package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.CreateSubtaskRequest;
import com.example.TaskService.controller.request.UpdateSubtaskRequest;
import com.example.TaskService.controller.response.SubtaskCreatedResponse;
import com.example.TaskService.controller.response.SubtaskResponse;
import com.example.TaskService.controller.response.UpdatedSubtaskResponse;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.service.dto.CreateSubtaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubtaskMapper {
    public CreateSubtaskDto subtaskRequestToCreateSubtaskDto(CreateSubtaskRequest taskRequest){
        CreateSubtaskDto dto = new CreateSubtaskDto();
        dto.setSubtaskName(taskRequest.getSubtaskName());
        dto.setEndTime(taskRequest.getEndTime());
        dto.setDescription(taskRequest.getDescription());
        dto.setTimeToSpend(taskRequest.getTimeToSpend());
        return dto;
    }

    public Subtask createSubtaskDtoToEntity(CreateSubtaskDto dto){
        Subtask entity = new Subtask();
        entity.setSubtaskName(dto.getSubtaskName());
        entity.setEndTime(dto.getEndTime());
        entity.setDescription(dto.getDescription());
        entity.setTimeToSpend(dto.getTimeToSpend());
        return entity;
    }

    public SubtaskDto updateSubtaskRequestToSubtaskDto(UpdateSubtaskRequest request){
        SubtaskDto dto = new SubtaskDto();
        dto.setSubtaskName(request.getSubtaskName());
        dto.setDescription(request.getDescription());
        dto.setEndTime(request.getEndTime());
        dto.setTimeToSpend(request.getTimeToSpend());
        dto.setIsComplete(request.getIsComplete());
        return dto;
    }

    public SubtaskDto subtaskEntityToSubtaskDto(Subtask entity) {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(entity.getId());
        dto.setTaskId(entity.getTask().getId());
        dto.setSubtaskName(entity.getSubtaskName());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDescription(entity.getDescription());
        dto.setTimeToSpend(entity.getTimeToSpend());
        dto.setTimeSpent(entity.getTimeSpent());
        dto.setIsComplete(entity.getIsComplete());
        return dto;
    }

    public List<SubtaskDto> subtaskEntitiesToSubtaskDtos(List<Subtask> entities) {
        return entities.stream()
                .map(this::subtaskEntityToSubtaskDto)
                .toList();
    }

    public UpdatedSubtaskResponse subtaskDtoToUpdatedSubtaskResponse(SubtaskDto dto){
        UpdatedSubtaskResponse response = new UpdatedSubtaskResponse();

        response.setSubtaskId(dto.getId());
        response.setTaskId(dto.getTaskId());
        response.setSubtaskName(dto.getSubtaskName());
        response.setDescription(dto.getDescription());
        response.setCreatedTime(dto.getCreatedTime());
        response.setEndTime(dto.getEndTime());
        response.setTimeToSpend(dto.getTimeToSpend());
        response.setTimeSpent(dto.getTimeSpent());
        response.setIsComplete(dto.getIsComplete());
        return response;
    }

    public SubtaskCreatedResponse subtaskDtoToCreatedResponse(SubtaskDto dto){
        SubtaskCreatedResponse response = new SubtaskCreatedResponse();
        response.setId(dto.getId());
        response.setTaskId(dto.getTaskId());
        response.setCreatedTime(dto.getCreatedTime());
        return response;
    }

    public SubtaskResponse subtaskDtoToResponse(SubtaskDto dto){
        SubtaskResponse response = new SubtaskResponse();
        response.setId(dto.getId());
        response.setTaskId(dto.getTaskId());
        response.setSubtaskName(dto.getSubtaskName());
        response.setDescription(dto.getDescription());
        response.setCreatedTime(dto.getCreatedTime());
        response.setEndTime(dto.getEndTime());
        response.setTimeToSpend(dto.getTimeToSpend());
        response.setTimeSpent(dto.getTimeSpent());
        response.setIsComplete(dto.getIsComplete());
        return response;
    }

    public List<SubtaskResponse> subtaskDtosToSubtaskResponses(List<SubtaskDto> dtos){
        return dtos.stream()
                .map(this::subtaskDtoToResponse)
                .toList();
    }
}
