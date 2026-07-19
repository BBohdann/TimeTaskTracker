package com.example.TaskService.service.service;

import com.example.TaskService.controller.request.subtask.SubtaskStatusRequest;
import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.SubtaskRepository;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.subtask.CreateSubtaskDto;
import com.example.TaskService.service.dto.subtask.SubtaskDto;
import com.example.TaskService.service.dto.subtask.UpdateSubtaskDto;
import com.example.TaskService.service.exception.SubtaskNotFoundException;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.SubtaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return subtaskMapper.subtaskEntityToSubtaskDto(subtasks);
    }

    @Transactional
    public SubtaskDto createSubtask(Long taskId, Long userId, CreateSubtaskDto dto) {
        Task task = findTaskOrThrow(taskId, userId);
        Subtask entity = subtaskMapper.createSubtaskDtoToEntity(dto);
        entity.setTask(task);

        return subtaskMapper.subtaskEntityToSubtaskDto(subtaskRepository.save(entity));
    }

    @Transactional
    public void updateSubtaskTimeSpent(Long taskId, Long subtaskId, Long userId, Integer timeSpent) {
        if (timeSpent == null || timeSpent < 0) {
            throw new IllegalArgumentException("timeSpent must be a non-negative number");
        }

        Subtask subtask = findOwnedSubtaskOrThrow(taskId, subtaskId, userId);
        subtask.setTimeSpent(subtask.getTimeSpent() + timeSpent);

        Task task = subtask.getTask();
        task.setTimeSpent(task.getTimeSpent() + timeSpent);
    }

    @Transactional
    public SubtaskDto updateSubtask(Long taskId, Long subtaskId, Long userId, UpdateSubtaskDto dto) {
        Subtask existing = findOwnedSubtaskOrThrow(taskId, subtaskId, userId);
        subtaskMapper.updateSubtaskFromDto(dto, existing);

        return subtaskMapper.subtaskEntityToSubtaskDto(existing);
    }

    @Transactional
    public void deleteSubtask(Long subtaskId, Long taskId, Long userId) {
        Subtask subtask = findOwnedSubtaskOrThrow(taskId, subtaskId, userId);

        decrementTaskTimeToSpend(subtask.getTask(), subtask.getTimeToSpend());
        subtaskRepository.delete(subtask);
    }

    public Task findTaskOrThrow(Long taskId, Long userId) {
        return taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    public Subtask findOwnedSubtaskOrThrow(Long taskId, Long subtaskId, Long userId) {
        return subtaskRepository
                .findOwnedSubtask(taskId, subtaskId, userId)
                .orElseThrow(() ->
                        new SubtaskNotFoundException(subtaskId));
    }

    public void assertSubtaskExists(Long subtaskId, Long taskId, Long userId) {
        if (!subtaskRepository.existsByIdAndTaskIdAndTaskUserId(subtaskId, taskId, userId)) {
            throw new SubtaskNotFoundException(subtaskId);
        }
    }

    private void decrementTaskTimeToSpend(Task task, Integer subtaskTimeToSpend) {
        if (task.getTimeToSpend() == null || subtaskTimeToSpend == null) {
            return;
        }

        task.setTimeToSpend(Math.max(task.getTimeToSpend() - subtaskTimeToSpend, 1));
        taskRepository.save(task);
    }
}