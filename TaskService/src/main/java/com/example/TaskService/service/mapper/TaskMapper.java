package com.example.TaskService.service.mapper;

import com.example.TaskService.controller.request.task.CreateTaskRequest;
import com.example.TaskService.controller.request.task.UpdateTaskRequest;
import com.example.TaskService.controller.response.task.TaskCreatedResponse;
import com.example.TaskService.controller.response.task.TaskResponse;
import com.example.TaskService.controller.response.task.TaskUpdatedResponse;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.service.dto.task.*;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    TaskWithSubtasksDto taskEntityToTaskWithSubtasksDto(Task entity);

    TaskDto taskEntityToTaskDto(Task entity);

    List<TaskWithSubtasksDto> taskEntityToTaskWithSubtasksDto(Collection<Task> entities);

    @Mapping(source = "taskRequest.taskName", target = "taskName")
    @Mapping(source = "taskRequest.endTime", target = "endTime")
    @Mapping(source = "taskRequest.description", target = "description")
    @Mapping(source = "taskRequest.timeToSpend", target = "timeToSpend")
    @Mapping(source = "userId", target = "userId")
    CreateTaskDto taskRequestToCreateTaskDto(CreateTaskRequest taskRequest, Long userId);

    Task createTaskToTaskEntity(CreateTaskDto createTaskDto);

    TaskCreatedResponse taskDtoToTaskCreatedResponse(TaskDto dto);

    TaskUpdatedResponse taskDtoToTaskUpdatedResponse(TaskDto dto);

    TaskResponse taskWithSubtasksDtoToTaskResponse(TaskWithSubtasksDto dto);

    UpdateTaskDto updateTaskRequestToUpdateTaskDto(UpdateTaskRequest request);

    List<TaskResponse> taskWithSubtaskDtoToTaskResponse(List<TaskWithSubtasksDto> dtos);

    void updateTaskFromDto(UpdateTaskDto dto, @MappingTarget Task task);
}