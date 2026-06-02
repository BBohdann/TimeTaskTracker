package com.example.TaskService.service.service;

import com.example.TaskService.controller.request.SubtaskStatusRequest;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.SubtaskRepository;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.CreateSubtaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import com.example.TaskService.service.exception.SubtaskNotFoundException;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.SubtaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubtaskService {
    private final SubtaskRepository subtaskRepository;
    private final SubtaskMapper subtaskMapper;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public SubtaskDto getSubtaskById(Long taskId, Long subtaskId, Long userId) {
        Subtask subtask = findOwnedSubtaskOrThrow(
                taskId,
                subtaskId,
                userId
        );

        return subtaskMapper.subtaskEntityToSubtaskDto(subtask);
    }

    @Transactional(readOnly = true)
    public List<SubtaskDto> getSubtasksByStatus(Long taskId,Long userId, SubtaskStatusRequest status) {
        List<Subtask> subtasks = switch (status) {
            case ACTIVE ->
                    subtaskRepository.findActiveOwnedSubtasks(
                            taskId,
                            userId
                    );
            case ALL ->
                    subtaskRepository.findAllOwnedSubtasks(
                            taskId,
                            userId
                    );
            case INACTIVE ->
                    subtaskRepository.findInactiveOwnedSubtasks(
                            taskId,
                            userId
                    );
        };

        return subtaskMapper.subtaskEntitiesToSubtaskDtos(subtasks);
    }

    @Transactional
    public SubtaskDto createSubtask(Long taskId, Long userId, CreateSubtaskDto dto) {
        Task task = findTaskOrThrow(taskId, userId);
        Subtask entity = subtaskMapper.createSubtaskDtoToEntity(dto);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setTask(task);

        return subtaskMapper.subtaskEntityToSubtaskDto(subtaskRepository.save(entity));
    }

    @Transactional
    public void updateSubtaskTimeSpent(Long taskId, Long subtaskId, Long userId, Integer timeSpent) {
        Subtask subtask = findOwnedSubtaskOrThrow(taskId, subtaskId, userId);
        subtask.setTimeSpent(subtask.getTimeSpent() + timeSpent);

        Task task = subtask.getTask();
        task.setTimeSpent(task.getTimeSpent() + timeSpent);
    }

    @Transactional
    public SubtaskDto updateSubtask(Long taskId, Long subtaskId, Long userId, SubtaskDto dto) {
        Subtask existing = findOwnedSubtaskOrThrow(taskId, subtaskId, userId);

        Optional.ofNullable(dto.getSubtaskName()).ifPresent(existing::setSubtaskName);
        Optional.ofNullable(dto.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(dto.getEndTime()).ifPresent(existing::setEndTime);
        Optional.ofNullable(dto.getTimeToSpend()).ifPresent(existing::setTimeToSpend);
        Optional.ofNullable(dto.getIsComplete()).ifPresent(existing::setIsComplete);

        return subtaskMapper.subtaskEntityToSubtaskDto(existing);
    }

    @Transactional
    public void deleteSubtask(Long subtaskId, Long taskId, Long userId) {
        Subtask subtask = findOwnedSubtaskOrThrow(taskId, subtaskId, userId);
        subtaskRepository.delete(subtask);
    }

    private Task findTaskOrThrow(Long taskId, Long userId) {
        return taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private Subtask findOwnedSubtaskOrThrow(Long taskId,
            Long subtaskId,
            Long userId) throws SubtaskNotFoundException {

        return subtaskRepository
                .findOwnedSubtask(taskId, subtaskId, userId)
                .orElseThrow(() ->
                        new SubtaskNotFoundException(subtaskId));
    }
}