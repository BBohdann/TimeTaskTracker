package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.CreateTaskRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.controller.responce.TaskBaseResponse;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.service.dto.CreateTaskDto;
import com.example.TaskService.service.dto.SubtaskMainInfoDto;
import com.example.TaskService.service.dto.TaskDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskMapper {
    public TaskDto toTaskDto(Task entity){
        TaskDto dto = new TaskDto();
        dto.setId(entity.getId());
        dto.setTaskName(entity.getTaskName());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDescription(entity.getDescription());
        dto.setTimeToSpend(entity.getTimeToSpend());
        dto.setTimeSpent(entity.getTimeSpent());
        dto.setIsComplete(entity.getIsComplete());

        List<SubtaskMainInfoDto> subtaskDtos = Optional.ofNullable(entity.getSubtasks())
                .orElse(Collections.emptyList())
                .stream()
                .map(subtask -> {
                    SubtaskMainInfoDto subtaskDto = new SubtaskMainInfoDto();
                    subtaskDto.setId(subtask.getId());
                    subtaskDto.setSubtaskName(subtask.getSubtaskName());
                    subtaskDto.setTimeSpent(subtask.getTimeSpent());
                    subtaskDto.setIsCompleate(subtask.getIsComplete());
                    subtaskDto.setEndTime(subtask.getEndTime());
                    return subtaskDto;
                })
                .collect(Collectors.toList());

        dto.setSubtasks(subtaskDtos);

        return dto;
    }

    public List<TaskDto> toTaskDtos(Collection<Task> entities) {
        return entities.stream()
                .map(this::toTaskDto)
                .collect(Collectors.toList());
    }

    public TaskDto updateTaskRequestToTaskDto(UpdateTaskRequest request, Long userId){
        TaskDto dto = new TaskDto();
        dto.setId(request.getId());
        dto.setTaskName(request.getTaskName());
        dto.setEndTime(request.getEndTime());
        dto.setDescription(request.getDescription());
        dto.setTimeToSpend(request.getTimeToSpend());
        dto.setIsComplete(request.getIsComplete());
        dto.setUserId(userId);
        return dto;
    }

    public CreateTaskDto taskRequestToDto(CreateTaskRequest taskRequest, Long userId){
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
        entity.setCreatedTime(createTaskDto.getCreatedTime());
        entity.setUserId(createTaskDto.getUserId());
        entity.setTaskName(createTaskDto.getTaskName());
        entity.setEndTime(createTaskDto.getEndTime());
        entity.setDescription(createTaskDto.getDescription());
        entity.setTimeToSpend(createTaskDto.getTimeToSpend());
        return entity;
    }

    public TaskBaseResponse toTaskBaseResponse(TaskDto dto) {
        TaskBaseResponse responce = new TaskBaseResponse();
        responce.setId(dto.getId());
        responce.setTaskName(dto.getTaskName());
        responce.setCreatedTime(dto.getCreatedTime());
        responce.setEndTime(dto.getEndTime());
        responce.setDescription(dto.getDescription());
        responce.setTimeToSpend(dto.getTimeToSpend());
        responce.setTimeSpent(dto.getTimeSpent());
        responce.setIsComplete(dto.getIsComplete());
        return responce;
    }
}