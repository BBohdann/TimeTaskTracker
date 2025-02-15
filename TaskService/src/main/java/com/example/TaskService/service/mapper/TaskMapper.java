package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.CreateTaskRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.service.dto.CreateTaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import com.example.TaskService.service.dto.SubtaskMainInfoDto;
import com.example.TaskService.service.dto.TaskDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {
    public TaskDto toTaskDto(Task entity){
        TaskDto dto = new TaskDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTaskName(entity.getTaskName());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDescription(entity.getDescription());
        dto.setTimeToSpend(entity.getTimeToSpend());
        dto.setTimeSpent(entity.getTimeSpent());
        dto.setIsComplete(entity.getIsComplete());

        List<SubtaskMainInfoDto> subtaskDtos = entity.getSubtasks().stream()
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

    public Task toTaskEntity(TaskDto dto){
        Task entity = new Task();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setTaskName(dto.getTaskName());
        entity.setEndTime(dto.getEndTime());
        entity.setDescription(dto.getDescription());
        entity.setTimeToSpend(dto.getTimeToSpend());
        entity.setTimeSpent(dto.getTimeSpent());
        return entity;
    }
}
