package com.example.TaskService.service.service;

import com.example.TaskService.controller.request.TaskStatusRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.CreateTaskDto;
import com.example.TaskService.service.dto.TaskDto;
import com.example.TaskService.service.dto.TaskWithSubtasksDto;
import com.example.TaskService.service.dto.UpdateTaskDto;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto createTask(CreateTaskDto dto) {
        dto.setCreatedTime(LocalDateTime.now());
        Task savedTask = taskRepository.save(taskMapper.createTaskToTaskEntity(dto));

        return taskMapper.taskEntityToTaskDto(savedTask);
    }

    @Transactional
    public void updateTimeSpent(Long taskId, Integer timeSpent, Long userId) {
        Task task = findTask(taskId, userId);

        task.setTimeSpent(
                task.getTimeSpent() + timeSpent
        );
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskDto dto, Long userId) {
        Task task = findTask(taskId, userId);

        Optional.ofNullable(dto.getTaskName()).ifPresent(task::setTaskName);
        Optional.ofNullable(dto.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(dto.getEndTime()).ifPresent(task::setEndTime);
        Optional.ofNullable(dto.getIsComplete()).ifPresent(task::setIsComplete);
        Optional.ofNullable(dto.getTimeToSpend()).ifPresent(task::setTimeToSpend);

        return taskMapper.taskEntityToTaskDto(task);
    }

    @Transactional(readOnly = true)
    public TaskWithSubtasksDto getTaskById(Long taskId, Long userId) {
        return taskMapper.taskEntityToTaskWithSubtasksDto(findTask(taskId, userId));
    }

    @Transactional(readOnly = true)
    public List<TaskWithSubtasksDto> getTasksByStatus(Long userId, TaskStatusRequest status) {
        List<Task> tasks = switch (status) {
            case ACTIVE ->
                    taskRepository.findActiveTasksByUserId(userId);
            case ALL ->
                    taskRepository.findTasksByUserId(userId);
            case INACTIVE ->
                    taskRepository.findInactiveTasksByUserId(userId);
        };

        return taskMapper.taskEntitiesToTaskWithSubtasksDtos(tasks);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        Task task = findTask(id, userId);
        taskRepository.delete(task);
    }

    private Task findTask(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}