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
        Subtask subtask = findSubtaskByIdOrThrow(id);
        return subtaskMapper.entityToSubtaskDto(subtask);
    }

    @Transactional
    public SubtaskDto addSubtask(CreateSubtaskDto dto) throws TaskNotFoundException {
        validateTaskExistence(dto.getTaskId());

        dto.setCreatedTime(LocalDateTime.now());
        Subtask saved = subtaskRepository.save(subtaskMapper.createSubtaskDtoToEntity(dto));
        return subtaskMapper.entityToSubtaskDto(saved);
    }

    @Transactional
    public void updateSubtaskTimeSpent(SubtaskDto dto) throws TaskNotFoundException, SubtaskNotFoundException {
        findSubtaskByIdOrThrow(dto.getId());

        subtaskRepository.updateTimeSpent(dto.getId(), dto.getTimeSpent());

        Long taskId = subtaskRepository.findTaskIdBySubtaskId(dto.getId())
                .orElseThrow(() -> new TaskNotFoundException(
                        Optional.ofNullable(dto.getTaskId()).map(Object::toString).orElse("Unknown Task ID")));

        taskRepository.updateTimeSpent(taskId, dto.getTimeSpent());
    }

    @Transactional
    public void changeIsCompleteFlag(Long subtaskId) throws SubtaskNotFoundException {
        int updated = subtaskRepository.changeIsCompleteFlag(subtaskId);
        if (updated == 0) {
            throw new SubtaskNotFoundException(subtaskId.toString());
        }
    }

    @Transactional
    public void updateSubtask(SubtaskDto dto) throws SubtaskNotFoundException {
        Subtask existing = findSubtaskByIdOrThrow(dto.getId());

        Optional.ofNullable(dto.getSubtaskName()).ifPresent(existing::setSubtaskName);
        Optional.ofNullable(dto.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(dto.getEndTime()).ifPresent(existing::setEndTime);
        Optional.ofNullable(dto.getTimeToSpend()).ifPresent(existing::setTimeToSpend);
        Optional.ofNullable(dto.getIsComplete()).ifPresent(existing::setIsComplete);

        subtaskRepository.save(existing);
    }

    @Transactional
    public void deleteSubtask(Long id) throws SubtaskNotFoundException {
        findSubtaskByIdOrThrow(id);
        subtaskRepository.deleteById(id);
    }

    private void validateTaskExistence(Long taskId) throws TaskNotFoundException {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Task with ID " + taskId + " not found");
        }
    }

    private Subtask findSubtaskByIdOrThrow(Long id) throws SubtaskNotFoundException {
        return subtaskRepository.findById(id)
                .orElseThrow(() -> new SubtaskNotFoundException("Subtask with ID " + id + " not found"));
    }
}