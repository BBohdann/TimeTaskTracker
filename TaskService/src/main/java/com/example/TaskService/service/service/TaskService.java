package com.example.TaskService.service.service;

import com.example.TaskService.controller.request.TaskStatusRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.CreateTaskDto;
import com.example.TaskService.service.dto.TaskDto;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto addTask(CreateTaskDto dto) {
        dto.setCreatedTime(LocalDateTime.now());
        Task savedTask = taskRepository.save(taskMapper.createTaskToTaskEntity(dto));

        return taskMapper.toTaskDto(savedTask);
    }

    @Transactional
    public TaskDto updateTimeSpent(Long taskId, Integer timeSpent, Long userId) {
        Task task = validateTaskExistence(taskId, userId);
        task.setTimeSpent(task.getTimeSpent()+timeSpent);

        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskRequest request, Long userId) {
        Task task = validateTaskExistence(taskId, userId);
        updateTaskFields(task, request);

        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findTasksByUserId(userId);
    }

    @Transactional
    public List<Task> getActiveTasksByUserId(Long userId) {
        return taskRepository.findActiveTasksByUserId(userId);
    }

    @Transactional
    public TaskDto getTaskById(Long id) {
        return fetchTaskDtoById(id);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        validateTaskExistence(id, userId);
        taskRepository.deleteById(id);
    }

    private Task validateTaskExistence(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private TaskDto fetchTaskDtoById(Long taskId) {
        return taskRepository.findById(taskId)
                .map(taskMapper::toTaskDto)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Transactional
    public List<TaskDto> getTasksByStatus(Long userId, TaskStatusRequest status) {
        List<Task> tasks = switch (status) {
            case ACTIVE ->
                    taskRepository.findActiveTasksByUserId(userId);
            case ALL ->
                    taskRepository.findTasksByUserId(userId);
            case INACTIVE ->
                    taskRepository.findTasksByUserId(userId);
        };

        return taskMapper.toTaskDtos(tasks);
    }

    private void updateTaskFields(Task task, UpdateTaskRequest request) {
        if (request.getTaskName() != null) {
            task.setTaskName(request.getTaskName());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getEndTime() != null) {
            task.setEndTime(request.getEndTime());
        }
        if (request.getTimeToSpend() != null) {
            task.setTimeToSpend(request.getTimeToSpend());
        }
        if (request.getIsComplete() != null) {
            task.setIsComplete(request.getIsComplete());
        }
    }
}