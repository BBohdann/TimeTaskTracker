package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.CreateTaskRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.controller.response.TaskCreatedResponse;
import com.example.TaskService.controller.response.TaskResponse;
import com.example.TaskService.controller.response.TaskUpdatedResponse;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.service.dto.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskMapper {
    public TaskWithSubtasksDto taskEntityToTaskWithSubtasksDto(Task entity){
        TaskWithSubtasksDto dto = new TaskWithSubtasksDto();
        dto.setId(entity.getId());
        dto.setTaskName(entity.getTaskName());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDescription(entity.getDescription());
        dto.setTimeToSpend(entity.getTimeToSpend());
        dto.setTimeSpent(entity.getTimeSpent());
        dto.setIsComplete(entity.getIsComplete());

        List<SubtaskMainInfoDto> subtasks = subtaskEntitiesToSubtaskMainInfoDtos(
                Optional.ofNullable(entity.getSubtasks())
                        .orElse(Collections.emptyList())
        );

        dto.setSubtasks(subtasks);

        return dto;
    }

    public TaskDto taskEntityToTaskDto(Task entity) {
        TaskDto dto = new TaskDto();
        dto.setId(entity.getId());
        dto.setTaskName(entity.getTaskName());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDescription(entity.getDescription());
        dto.setTimeToSpend(entity.getTimeToSpend());
        dto.setTimeSpent(entity.getTimeSpent());
        dto.setIsComplete(entity.getIsComplete());

        return dto;
    }

    public List<TaskWithSubtasksDto> taskEntitiesToTaskWithSubtasksDtos(Collection<Task> entities) {
        return entities.stream()
                .map(this::taskEntityToTaskWithSubtasksDto)
                .collect(Collectors.toList());
    }


    public CreateTaskDto taskRequestToCreateTaskDto(CreateTaskRequest taskRequest, Long userId){
        CreateTaskDto dto = new CreateTaskDto();
        dto.setTaskName(taskRequest.getTaskName());
        dto.setEndTime(taskRequest.getEndTime());
        dto.setDescription(taskRequest.getDescription());
        dto.setTimeToSpend(taskRequest.getTimeToSpend());
        dto.setUserId(userId);
        return dto;
    }

    public Task createTaskToTaskEntity(CreateTaskDto createTaskDto){
        Task entity = new Task();
        entity.setUserId(createTaskDto.getUserId());
        entity.setTaskName(createTaskDto.getTaskName());
        entity.setEndTime(createTaskDto.getEndTime());
        entity.setDescription(createTaskDto.getDescription());
        entity.setTimeToSpend(createTaskDto.getTimeToSpend());
        return entity;
    }

    public TaskCreatedResponse taskDtoToTaskCreatedResponse(TaskDto dto) {
        TaskCreatedResponse response = new TaskCreatedResponse();
        response.setId(dto.getId());
        response.setCreatedTime(dto.getCreatedTime());
        return response;
    }

    public TaskUpdatedResponse taskDtoToTaskUpdatedResponse(TaskDto dto) {
        TaskUpdatedResponse response = new TaskUpdatedResponse();

        response.setId(dto.getId());
        response.setTaskName(dto.getTaskName());
        response.setDescription(dto.getDescription());
        response.setCreatedTime(dto.getCreatedTime());
        response.setEndTime(dto.getEndTime());
        response.setTimeToSpend(dto.getTimeToSpend());
        response.setTimeSpent(dto.getTimeSpent());
        response.setIsComplete(dto.getIsComplete());

        return response;
    }

    public TaskResponse taskWithSubtasksDtoToTaskResponse(TaskWithSubtasksDto dto) {
        TaskResponse response = new TaskResponse();

        response.setId(dto.getId());
        response.setTaskName(dto.getTaskName());
        response.setDescription(dto.getDescription());
        response.setCreatedTime(dto.getCreatedTime());
        response.setEndTime(dto.getEndTime());
        response.setTimeToSpend(dto.getTimeToSpend());
        response.setTimeSpent(dto.getTimeSpent());
        response.setIsComplete(dto.getIsComplete());
        response.setSubtasks(dto.getSubtasks());

        return response;
    }

    public UpdateTaskDto updateTaskRequestToUpdateTaskDto(UpdateTaskRequest request){
        UpdateTaskDto dto = new UpdateTaskDto();

        dto.setTaskName(request.getTaskName());
        dto.setDescription(request.getDescription());
        dto.setEndTime(request.getEndTime());
        dto.setTimeToSpend(request.getTimeToSpend());
        dto.setIsComplete(request.getIsComplete());

        return dto;
    }

    public List<TaskResponse> taskWithSubtasksDtosToTaskResponses (List<TaskWithSubtasksDto> dtos){
        return dtos.stream()
                .map(this::taskWithSubtasksDtoToTaskResponse)
                .toList();
    }

    private SubtaskMainInfoDto subtaskEntityToSubtaskMainInfoDto(Subtask entity) {
        SubtaskMainInfoDto dto = new SubtaskMainInfoDto();

        dto.setId(entity.getId());
        dto.setSubtaskName(entity.getSubtaskName());
        dto.setTimeSpent(entity.getTimeSpent());
        dto.setTimeToSpent(entity.getTimeToSpend());
        dto.setIsComplete(entity.getIsComplete());
        dto.setEndTime(entity.getEndTime());

        return dto;
    }

    private List<SubtaskMainInfoDto> subtaskEntitiesToSubtaskMainInfoDtos(List<Subtask> entities) {
        return entities.stream()
                .map(this::subtaskEntityToSubtaskMainInfoDto)
                .toList();
    }
}