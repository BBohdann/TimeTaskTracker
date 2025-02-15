package com.example.TaskService.service.service;

import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.CreateTaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import com.example.TaskService.service.dto.TaskDto;
import com.example.TaskService.service.exception.SubtaskNotFoundException;
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
    public Long addTask(CreateTaskDto dto) {
        dto.setCreatedTime(LocalDateTime.now());
        return taskRepository.save(taskMapper.createTaskToTaskEntity(dto)).getId();
    }

    @Transactional
    public void updateTimeSpent(TaskDto dto) throws TaskNotFoundException {
        if(!taskRepository.existsByTaskIdAndUserId (dto.getId(), dto.getUserId()))
            throw new TaskNotFoundException(dto.getId().toString());
        taskRepository.updateTimeSpent(dto.getId(), dto.getTimeSpent());
    }

    @Transactional
    public void updateTask(TaskDto dto) throws TaskNotFoundException {
        Task existingTask = taskRepository.findByIdAndUserId(dto.getId(), dto.getUserId())
                .orElseThrow(() -> new TaskNotFoundException(dto.getId().toString()));

        Optional.ofNullable(dto.getTaskName()).ifPresent(existingTask::setTaskName);
        Optional.ofNullable(dto.getDescription()).ifPresent(existingTask::setDescription);
        Optional.ofNullable(dto.getEndTime()).ifPresent(existingTask::setEndTime);
        Optional.ofNullable(dto.getTimeToSpend()).ifPresent(existingTask::setTimeToSpend);
        Optional.ofNullable(dto.getTimeSpent()).ifPresent(existingTask::setTimeSpent);
        Optional.ofNullable(dto.getIsComplete()).ifPresent(existingTask::setIsComplete);
        taskRepository.save(existingTask);
    }

    @Transactional
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findTasksByUserId(userId);
    }

    @Transactional
    public List<Task> getActiveTasksByUserId(Long userId) {
        return taskRepository.findActiveTasksByUserId(userId);
    }

    public TaskDto getTaskById(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id.toString()));
        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) throws TaskNotFoundException {
        if(!taskRepository.existsByTaskIdAndUserId(id, userId))
            throw new TaskNotFoundException(id.toString());
        taskRepository.deleteById(id);
    }
}