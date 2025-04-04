package com.example.TaskService.service.service;

import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.SubtaskRepository;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.CreateSubtaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import com.example.TaskService.service.exception.SubtaskNotFoundException;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.SubtaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubtaskServiceTest {
    private SubtaskService subtaskService;
    private SubtaskRepository subtaskRepository;
    private SubtaskMapper subtaskMapper;
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        subtaskRepository = mock(SubtaskRepository.class);
        taskRepository = mock(TaskRepository.class);
        subtaskMapper = new SubtaskMapper();
        subtaskService = new SubtaskService(subtaskRepository, subtaskMapper, taskRepository);
    }

    @Test
    void getSubtasksByTaskId_Success() {
        when(subtaskRepository.findByTaskId(1L)).thenReturn(List.of(new Subtask(), new Subtask()));

        List<Subtask> subtasks = subtaskService.getSubtasksByTaskId(1L);

        assertEquals(2, subtasks.size());
        verify(subtaskRepository).findByTaskId(1L);
    }

    @Test
    void getSubtaskById_Success() throws SubtaskNotFoundException {
        Task task = new Task();
        task.setId(10L);

        Subtask subtask = new Subtask();
        subtask.setId(1L);
        subtask.setTask(task);

        when(subtaskRepository.findById(1L)).thenReturn(Optional.of(subtask));

        SubtaskDto result = subtaskService.getSubtaskById(1L);

        assertEquals(1L, result.getId());
        assertEquals(10L, result.getTaskId());

        verify(subtaskRepository).findById(1L);
    }


    @Test
    void getSubtaskById_NotFound() {
        when(subtaskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SubtaskNotFoundException.class, () -> subtaskService.getSubtaskById(1L));

        verify(subtaskRepository).findById(1L);
    }

    @Test
    void addSubtask_Success() throws TaskNotFoundException {
        CreateSubtaskDto dto = new CreateSubtaskDto();
        dto.setTaskId(1L);
        dto.setSubtaskName("Test Subtask");

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(subtaskRepository.save(any(Subtask.class))).thenAnswer(invocation -> {
            Subtask savedSubtask = invocation.getArgument(0);
            savedSubtask.setId(1L);
            return savedSubtask;
        });

        SubtaskDto result = subtaskService.addSubtask(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(dto.getSubtaskName(), result.getSubtaskName());
        assertNotNull(result.getCreatedTime());

        verify(subtaskRepository).save(any(Subtask.class));
    }

    @Test
    void addSubtask_TaskNotFound() {
        CreateSubtaskDto dto = new CreateSubtaskDto();
        dto.setTaskId(1L);

        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> subtaskService.addSubtask(dto));

        verify(subtaskRepository, never()).save(any(Subtask.class));
    }

    @Test
    void updateSubtaskTimeSpent_Success() throws SubtaskNotFoundException, TaskNotFoundException {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(1L);
        dto.setTaskId(1L);
        dto.setTimeSpent(5);

        when(subtaskRepository.existsById(1L)).thenReturn(true);
        when(subtaskRepository.findTaskIdBySubtaskId(1L)).thenReturn(Optional.of(1L));

        subtaskService.updateSubtaskTimeSpent(dto);

        verify(subtaskRepository).updateTimeSpent(1L, 5);
        verify(taskRepository).updateTimeSpent(1L, 5);
    }

    @Test
    void updateSubtaskTimeSpent_SubtaskNotFound() {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(1L);

        when(subtaskRepository.existsById(1L)).thenReturn(false);

        assertThrows(SubtaskNotFoundException.class, () -> subtaskService.updateSubtaskTimeSpent(dto));

        verify(subtaskRepository, never()).updateTimeSpent(anyLong(), anyInt());
        verify(taskRepository, never()).updateTimeSpent(anyLong(), anyInt());
    }

    @Test
    void updateSubtaskTimeSpent_TaskNotFound() {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(1L);
        dto.setTaskId(999L);
        dto.setTimeSpent(5);

        when(subtaskRepository.existsById(1L)).thenReturn(true);
        when(subtaskRepository.findTaskIdBySubtaskId(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> subtaskService.updateSubtaskTimeSpent(dto));
    }

    @Test
    void changeIsCompleteFlag_Success() throws SubtaskNotFoundException {
        when(subtaskRepository.changeIsCompleteFlag(1L)).thenReturn(1);

        subtaskService.changeIsCompleteFlag(1L);

        verify(subtaskRepository).changeIsCompleteFlag(1L);
    }

    @Test
    void changeIsCompleteFlag_SubtaskNotFound() {
        when(subtaskRepository.changeIsCompleteFlag(1L)).thenReturn(0);

        assertThrows(SubtaskNotFoundException.class, () -> subtaskService.changeIsCompleteFlag(1L));

        verify(subtaskRepository).changeIsCompleteFlag(1L);
    }

    @Test
    void updateSubtask_Success() throws TaskNotFoundException {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(1L);
        dto.setSubtaskName("Updated Subtask");
        dto.setDescription("Updated Description");

        Subtask existingSubtask = new Subtask();
        existingSubtask.setId(dto.getId());

        when(subtaskRepository.findById(1L)).thenReturn(Optional.of(existingSubtask));
        when(subtaskRepository.save(any(Subtask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        subtaskService.updateSubtask(dto);

        assertEquals(dto.getSubtaskName(), existingSubtask.getSubtaskName());
        assertEquals(dto.getDescription(), existingSubtask.getDescription());
        verify(subtaskRepository).save(existingSubtask);
    }

    @Test
    void updateSubtask_SubtaskNotFound() {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(1L);

        when(subtaskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> subtaskService.updateSubtask(dto));

        verify(subtaskRepository, never()).save(any(Subtask.class));
    }

    @Test
    void deleteSubtask_Success() throws SubtaskNotFoundException {
        when(subtaskRepository.existsById(1L)).thenReturn(true);

        subtaskService.deleteSubtask(1L);

        verify(subtaskRepository).deleteById(1L);
    }

    @Test
    void deleteSubtask_SubtaskNotFound() {
        when(subtaskRepository.existsById(1L)).thenReturn(false);

        assertThrows(SubtaskNotFoundException.class, () -> subtaskService.deleteSubtask(1L));

        verify(subtaskRepository, never()).deleteById(anyLong());
    }
}
