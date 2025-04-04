package com.example.TaskService.service.service;

import com.example.TaskService.data.entity.Subtask;
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

    public List<Subtask> getSubtasksByTaskId(Long taskId) {
        return subtaskRepository.findByTaskId(taskId);
    }

    public SubtaskDto getSubtaskById(Long id) throws SubtaskNotFoundException {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new SubtaskNotFoundException(id.toString()));
        return subtaskMapper.entityToSubtaskDto(subtask);
    }

    @Transactional
    public SubtaskDto addSubtask(CreateSubtaskDto subtask) throws TaskNotFoundException {
        if (!taskRepository.existsById(subtask.getTaskId()))
            throw new TaskNotFoundException(subtask.getTaskId().toString());
        subtask.setCreatedTime(LocalDateTime.now());
        Subtask entity = subtaskMapper.createSubtaskDtoToEntity(subtask);
        return subtaskMapper.entityToSubtaskDto(subtaskRepository.save(entity));
    }

    @Transactional
    public void updateSubtaskTimeSpent(SubtaskDto dto) throws SubtaskNotFoundException, TaskNotFoundException {
        if (!subtaskRepository.existsById(dto.getId()))
            throw new SubtaskNotFoundException(dto.getId().toString());
        subtaskRepository.updateTimeSpent(dto.getId(), dto.getTimeSpent());

        Long taskId = subtaskRepository.findTaskIdBySubtaskId(dto.getId())
                .orElseThrow(() -> new TaskNotFoundException(
                        dto.getTaskId() != null ? dto.getTaskId().toString() : "Unknown Task ID"
                ));

        taskRepository.updateTimeSpent(taskId, dto.getTimeSpent());
    }

    @Transactional
    public void changeIsCompleteFlag(Long subtaskId) throws SubtaskNotFoundException {
        Optional.of(subtaskRepository.changeIsCompleteFlag(subtaskId))
                .filter(updatedRows -> updatedRows > 0)
                .orElseThrow(() -> new SubtaskNotFoundException(subtaskId.toString()));
    }

    @Transactional
    public void updateSubtask(SubtaskDto dto) throws TaskNotFoundException {
        Subtask existingTask = subtaskRepository.findById(dto.getId())
                .orElseThrow(() -> new TaskNotFoundException(dto.getId().toString()));

        Optional.ofNullable(dto.getSubtaskName()).ifPresent(existingTask::setSubtaskName);
        Optional.ofNullable(dto.getDescription()).ifPresent(existingTask::setDescription);
        Optional.ofNullable(dto.getEndTime()).ifPresent(existingTask::setEndTime);
        Optional.ofNullable(dto.getTimeToSpend()).ifPresent(existingTask::setTimeToSpend);
        Optional.ofNullable(dto.getIsComplete()).ifPresent(existingTask::setIsComplete);
        subtaskRepository.save(existingTask);
    }

    @Transactional
    public void deleteSubtask(Long id) throws SubtaskNotFoundException {
        if (!subtaskRepository.existsById(id))
            throw new SubtaskNotFoundException(id.toString());
        subtaskRepository.deleteById(id);
    }
}