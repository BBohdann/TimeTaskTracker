package com.example.TaskService.service.service;

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
    public TaskDto updateTimeSpent(TaskDto dto) throws TaskNotFoundException {
        validateTaskExistence(dto.getId(), dto.getUserId());
        taskRepository.updateTimeSpent(dto.getId(), dto.getTimeSpent());
        return fetchTaskDtoById(dto.getId());
    }

    @Transactional
    public TaskDto updateTask(TaskDto dto) throws TaskNotFoundException {
        Task existingTask = validateTaskExistence(dto.getId(), dto.getUserId());
        updateTaskFields(existingTask, dto);
        return taskMapper.toTaskDto(taskRepository.save(existingTask));
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findTasksByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Task> getActiveTasksByUserId(Long userId) {
        return taskRepository.findActiveTasksByUserId(userId);
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) throws TaskNotFoundException {
        return fetchTaskDtoById(id);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) throws TaskNotFoundException {
        validateTaskExistence(id, userId);
        taskRepository.deleteById(id);
    }

    private Task validateTaskExistence(Long taskId, Long userId) throws TaskNotFoundException {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + taskId + " not found"));
    }

    private TaskDto fetchTaskDtoById(Long taskId) throws TaskNotFoundException {
        return taskRepository.findById(taskId)
                .map(taskMapper::toTaskDto)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + taskId + " not found"));
    }

    private void updateTaskFields(Task existingTask, TaskDto dto) {
        if (dto.getTaskName() != null) existingTask.setTaskName(dto.getTaskName());
        if (dto.getDescription() != null) existingTask.setDescription(dto.getDescription());
        if (dto.getEndTime() != null) existingTask.setEndTime(dto.getEndTime());
        if (dto.getTimeToSpend() != null) existingTask.setTimeToSpend(dto.getTimeToSpend());
        if (dto.getIsComplete() != null) existingTask.setIsComplete(dto.getIsComplete());
    }
}