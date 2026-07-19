package com.example.TaskService.service.service;

import com.example.TaskService.controller.request.task.TaskStatusRequest;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.task.CreateTaskDto;
import com.example.TaskService.service.dto.task.TaskDto;
import com.example.TaskService.service.dto.task.TaskWithSubtasksDto;
import com.example.TaskService.service.dto.task.UpdateTaskDto;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto createTask(CreateTaskDto dto) {
        Task savedTask = taskRepository.save(taskMapper.createTaskToTaskEntity(dto));

        return taskMapper.taskEntityToTaskDto(savedTask);
    }

    @Transactional
    public void updateTimeSpent(Long taskId, Long userId, Integer timeSpent) {
        if (timeSpent == null || timeSpent < 0) {
            throw new IllegalArgumentException("timeSpent must be a non-negative number");
        }

        Task task = getTaskOrThrow(taskId, userId);
        task.setTimeSpent(task.getTimeSpent() + timeSpent);

        taskRepository.save(task);
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskDto dto, Long userId) {
        Task task = getTaskOrThrow(taskId, userId);
        taskMapper.updateTaskFromDto(dto, task);

        return taskMapper.taskEntityToTaskDto(task);
    }

    @Transactional(readOnly = true)
    public TaskWithSubtasksDto getTaskById(Long taskId, Long userId) {
        return taskMapper.taskEntityToTaskWithSubtasksDto(getTaskOrThrow(taskId, userId));
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

        return taskMapper.taskEntityToTaskWithSubtasksDto(tasks);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        Task task = getTaskOrThrow(id, userId);
        taskRepository.delete(task);
    }

    private Task getTaskOrThrow(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    public void assertTaskExists(Long taskId, Long userId) {
        if (!taskRepository.existsByIdAndUserId(taskId, userId)) {
            throw new TaskNotFoundException(taskId);
        }
    }
}