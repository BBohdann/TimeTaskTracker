package com.example.TaskService.service.service;

import com.example.TaskService.data.entity.Task;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.service.dto.CreateTaskDto;
import com.example.TaskService.service.dto.TaskDto;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    private TaskService taskService;
    private TaskRepository taskRepository;
    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskMapper = new TaskMapper();
        taskService = new TaskService(taskRepository, taskMapper);
    }

    @Test
    void addTask_Success() {
        CreateTaskDto dto = new CreateTaskDto();
        dto.setTaskName("taskName");
        dto.setDescription("test description");
        dto.setUserId(1L);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(1L);
            return savedTask;
        });

        TaskDto result = taskService.addTask(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(dto.getTaskName(), result.getTaskName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertNotNull(result.getCreatedTime());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTimeSpent_Success() throws TaskNotFoundException {
        TaskDto dto = new TaskDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTimeSpent(5);

        when(taskRepository.existsByTaskIdAndUserId(dto.getId(), dto.getUserId())).thenReturn(true);
        when(taskRepository.findById(dto.getId())).thenReturn(Optional.of(new Task()));

        TaskDto result = taskService.updateTimeSpent(dto);

        assertNotNull(result);
        verify(taskRepository).updateTimeSpent(dto.getId(), dto.getTimeSpent());
        verify(taskRepository).findById(dto.getId());
    }

    @Test
    void updateTimeSpent_TaskNotFound() {
        TaskDto dto = new TaskDto();
        dto.setId(1L);
        dto.setUserId(1L);

        when(taskRepository.existsByTaskIdAndUserId(dto.getId(), dto.getUserId())).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTimeSpent(dto));

        verify(taskRepository, never()).updateTimeSpent(anyLong(), anyInt());
    }

    @Test
    void updateTask_Success() throws TaskNotFoundException {
        TaskDto dto = new TaskDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTaskName("Updated Task");
        dto.setDescription("Updated Description");

        Task existingTask = new Task();
        existingTask.setId(dto.getId());
        existingTask.setUserId(dto.getUserId());

        when(taskRepository.findByIdAndUserId(dto.getId(), dto.getUserId())).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDto result = taskService.updateTask(dto);

        assertEquals(dto.getTaskName(), result.getTaskName());
        assertEquals(dto.getDescription(), result.getDescription());
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_TaskNotFound() {
        TaskDto dto = new TaskDto();
        dto.setId(1L);
        dto.setUserId(1L);

        when(taskRepository.findByIdAndUserId(dto.getId(), dto.getUserId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(dto));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTasksByUserId_Success() {
        when(taskRepository.findTasksByUserId(1L)).thenReturn(List.of(new Task(), new Task()));

        List<Task> tasks = taskService.getTasksByUserId(1L);

        assertEquals(2, tasks.size());
        verify(taskRepository).findTasksByUserId(1L);
    }

    @Test
    void getActiveTasksByUserId_Success() {
        when(taskRepository.findActiveTasksByUserId(1L)).thenReturn(List.of(new Task()));

        List<Task> tasks = taskService.getActiveTasksByUserId(1L);

        assertEquals(1, tasks.size());
        verify(taskRepository).findActiveTasksByUserId(1L);
    }

    @Test
    void getTaskById_Success() throws TaskNotFoundException {
        Task task = new Task();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDto result = taskService.getTaskById(1L);

        assertEquals(1L, result.getId());
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));

        verify(taskRepository).findById(1L);
    }

    @Test
    void deleteTask_Success() throws TaskNotFoundException {
        when(taskRepository.existsByTaskIdAndUserId(1L, 1L)).thenReturn(true);

        taskService.deleteTask(1L, 1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_TaskNotFound() {
        when(taskRepository.existsByTaskIdAndUserId(1L, 1L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L, 1L));

        verify(taskRepository, never()).deleteById(anyLong());
    }
}