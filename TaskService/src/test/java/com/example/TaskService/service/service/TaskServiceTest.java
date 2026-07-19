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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private static final Long USER_ID = 1L;
    private static final Long TASK_ID = 42L;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        existingTask = new Task();
        existingTask.setId(TASK_ID);
        existingTask.setUserId(USER_ID);
        existingTask.setTaskName("Original name");
        existingTask.setDescription("Original description");
        existingTask.setTimeSpent(10);
        existingTask.setTimeToSpend(60);
        existingTask.setIsComplete(false);
    }

    @Nested
    class CreateTask {

        @Test
        void savesEntityAndReturnsMappedDto() {
            CreateTaskDto createDto = new CreateTaskDto();
            createDto.setUserId(USER_ID);
            createDto.setTaskName("New task");

            Task entityToSave = new Task();
            Task savedEntity = new Task();
            savedEntity.setId(TASK_ID);
            TaskDto expectedDto = new TaskDto();
            expectedDto.setId(TASK_ID);

            when(taskMapper.createTaskToTaskEntity(createDto)).thenReturn(entityToSave);
            when(taskRepository.save(entityToSave)).thenReturn(savedEntity);
            when(taskMapper.taskEntityToTaskDto(savedEntity)).thenReturn(expectedDto);

            TaskDto result = taskService.createTask(createDto);

            assertThat(result).isEqualTo(expectedDto);
            verify(taskRepository).save(entityToSave);
        }
    }

    @Nested
    class UpdateTimeSpent {

        @Test
        void addsToExistingTimeSpentAndSaves() {
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(existingTask));

            taskService.updateTimeSpent(TASK_ID, USER_ID, 15);

            assertThat(existingTask.getTimeSpent()).isEqualTo(25);
            verify(taskRepository).save(existingTask);
        }

        @Test
        void throwsWhenAdditionalTimeIsNull() {
            assertThatThrownBy(() -> taskService.updateTimeSpent(TASK_ID, USER_ID, null))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(taskRepository);
        }

        @Test
        void throwsWhenAdditionalTimeIsNegative() {
            assertThatThrownBy(() -> taskService.updateTimeSpent(TASK_ID, USER_ID, -5))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(taskRepository);
        }

        @Test
        void throwsTaskNotFoundWhenTaskMissing() {
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.updateTimeSpent(TASK_ID, USER_ID, 15))
                    .isInstanceOf(TaskNotFoundException.class);
        }
    }

    @Nested
    class UpdateTask {

        @Test
        void updatesOnlyNonNullFieldsAndSaves() {
            UpdateTaskDto updateDto = new UpdateTaskDto();
            updateDto.setTaskName("Updated name");

            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(existingTask));

            doAnswer(invocation -> {
                UpdateTaskDto source = invocation.getArgument(0);
                Task target = invocation.getArgument(1);
                Optional.ofNullable(source.getTaskName()).ifPresent(target::setTaskName);
                Optional.ofNullable(source.getDescription()).ifPresent(target::setDescription);
                Optional.ofNullable(source.getEndTime()).ifPresent(target::setEndTime);
                Optional.ofNullable(source.getTimeToSpend()).ifPresent(target::setTimeToSpend);
                Optional.ofNullable(source.getIsComplete()).ifPresent(target::setIsComplete);
                return null;
            }).when(taskMapper).updateTaskFromDto(any(UpdateTaskDto.class), any(Task.class));

            TaskDto expectedDto = new TaskDto();
            when(taskMapper.taskEntityToTaskDto(existingTask)).thenReturn(expectedDto);

            TaskDto result = taskService.updateTask(TASK_ID, updateDto, USER_ID);

            assertThat(existingTask.getTaskName()).isEqualTo("Updated name");
            assertThat(existingTask.getDescription()).isEqualTo("Original description");
            assertThat(result).isEqualTo(expectedDto);
        }

        @Test
        void updatesIsCompleteEvenWhenFalse() {
            existingTask.setIsComplete(true);
            UpdateTaskDto updateDto = new UpdateTaskDto();
            updateDto.setIsComplete(false);

            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(existingTask));

            doAnswer(invocation -> {
                UpdateTaskDto source = invocation.getArgument(0);
                Task target = invocation.getArgument(1);
                Optional.ofNullable(source.getTaskName()).ifPresent(target::setTaskName);
                Optional.ofNullable(source.getDescription()).ifPresent(target::setDescription);
                Optional.ofNullable(source.getEndTime()).ifPresent(target::setEndTime);
                Optional.ofNullable(source.getTimeToSpend()).ifPresent(target::setTimeToSpend);
                Optional.ofNullable(source.getIsComplete()).ifPresent(target::setIsComplete);
                return null;
            }).when(taskMapper).updateTaskFromDto(any(UpdateTaskDto.class), any(Task.class));

            when(taskMapper.taskEntityToTaskDto(existingTask)).thenReturn(new TaskDto());

            taskService.updateTask(TASK_ID, updateDto, USER_ID);

            assertThat(existingTask.getIsComplete()).isFalse();
        }

        @Test
        void throwsTaskNotFoundWhenTaskDoesNotBelongToUser() {
            UpdateTaskDto updateDto = new UpdateTaskDto();
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.updateTask(TASK_ID, updateDto, USER_ID))
                    .isInstanceOf(TaskNotFoundException.class);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    class GetTaskById {

        @Test
        void returnsMappedTaskWhenFound() {
            TaskWithSubtasksDto expectedDto = new TaskWithSubtasksDto();
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(existingTask));
            when(taskMapper.taskEntityToTaskWithSubtasksDto(existingTask)).thenReturn(expectedDto);

            TaskWithSubtasksDto result = taskService.getTaskById(TASK_ID, USER_ID);

            assertThat(result).isEqualTo(expectedDto);
        }

        @Test
        void throwsTaskNotFoundWhenMissing() {
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.getTaskById(TASK_ID, USER_ID))
                    .isInstanceOf(TaskNotFoundException.class);
        }
    }

    @Nested
    class GetTasksByStatus {

        @Test
        void delegatesToActiveQueryForActiveStatus() {
            when(taskRepository.findActiveTasksByUserId(USER_ID)).thenReturn(List.of(existingTask));
            when(taskMapper.taskEntityToTaskWithSubtasksDto(List.of(existingTask)))
                    .thenReturn(List.of(new TaskWithSubtasksDto()));

            List<TaskWithSubtasksDto> result = taskService.getTasksByStatus(USER_ID, TaskStatusRequest.ACTIVE);

            assertThat(result).hasSize(1);
            verify(taskRepository).findActiveTasksByUserId(USER_ID);
            verify(taskRepository, never()).findTasksByUserId(any());
            verify(taskRepository, never()).findInactiveTasksByUserId(any());
        }

        @Test
        void delegatesToInactiveQueryForInactiveStatus() {
            when(taskRepository.findInactiveTasksByUserId(USER_ID)).thenReturn(List.of(existingTask));
            when(taskMapper.taskEntityToTaskWithSubtasksDto(List.of(existingTask)))
                    .thenReturn(List.of(new TaskWithSubtasksDto()));

            taskService.getTasksByStatus(USER_ID, TaskStatusRequest.INACTIVE);

            verify(taskRepository).findInactiveTasksByUserId(USER_ID);
        }

        @Test
        void delegatesToAllQueryForAllStatus() {
            when(taskRepository.findTasksByUserId(USER_ID)).thenReturn(List.of(existingTask));
            when(taskMapper.taskEntityToTaskWithSubtasksDto(List.of(existingTask)))
                    .thenReturn(List.of(new TaskWithSubtasksDto()));

            taskService.getTasksByStatus(USER_ID, TaskStatusRequest.ALL);

            verify(taskRepository).findTasksByUserId(USER_ID);
        }
    }

    @Nested
    class DeleteTask {

        @Test
        void deletesTaskWhenFound() {
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(existingTask));

            taskService.deleteTask(TASK_ID, USER_ID);

            verify(taskRepository).delete(existingTask);
        }

        @Test
        void throwsTaskNotFoundAndDoesNotDeleteWhenMissing() {
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.deleteTask(TASK_ID, USER_ID))
                    .isInstanceOf(TaskNotFoundException.class);

            verify(taskRepository, never()).delete(any());
        }
    }
}