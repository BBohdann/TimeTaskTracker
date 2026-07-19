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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubtaskServiceTest {
    @Mock
    private SubtaskRepository subtaskRepository;

    @Mock
    private SubtaskMapper subtaskMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SubtaskService subtaskService;

    private static final Long USER_ID = 1L;
    private static final Long TASK_ID = 10L;
    private static final Long SUBTASK_ID = 100L;

    private Task parentTask;
    private Subtask existingSubtask;

    @BeforeEach
    void setUp() {
        parentTask = new Task();
        parentTask.setId(TASK_ID);
        parentTask.setUserId(USER_ID);
        parentTask.setTimeSpent(50);

        existingSubtask = new Subtask();
        existingSubtask.setId(SUBTASK_ID);
        existingSubtask.setTask(parentTask);
        existingSubtask.setSubtaskName("Original name");
        existingSubtask.setDescription("Original description");
        existingSubtask.setTimeSpent(5);
        existingSubtask.setIsComplete(false);
    }

    @Nested
    class CreateSubtask {

        @Test
        void attachesParentTaskAndSaves() {
            CreateSubtaskDto createDto = new CreateSubtaskDto();
            createDto.setSubtaskName("New subtask");

            Subtask newEntity = new Subtask();
            Subtask savedEntity = new Subtask();
            savedEntity.setId(SUBTASK_ID);
            SubtaskDto expectedDto = new SubtaskDto();

            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(parentTask));
            when(subtaskMapper.createSubtaskDtoToEntity(createDto)).thenReturn(newEntity);
            when(subtaskRepository.save(newEntity)).thenReturn(savedEntity);
            when(subtaskMapper.subtaskEntityToSubtaskDto(savedEntity)).thenReturn(expectedDto);

            SubtaskDto result = subtaskService.createSubtask(TASK_ID, USER_ID, createDto);

            assertThat(newEntity.getTask()).isEqualTo(parentTask);
            assertThat(result).isEqualTo(expectedDto);
        }

        @Test
        void throwsTaskNotFoundWhenParentTaskMissing() {
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subtaskService.createSubtask(TASK_ID, USER_ID, new CreateSubtaskDto()))
                    .isInstanceOf(TaskNotFoundException.class);

            verifyNoInteractions(subtaskRepository);
        }
    }

    @Nested
    class UpdateSubtaskTimeSpent {

        @Test
        void incrementsBothSubtaskAndParentTaskTimeSpent() {
            when(subtaskRepository.findOwnedSubtask(TASK_ID, SUBTASK_ID, USER_ID))
                    .thenReturn(Optional.of(existingSubtask));

            subtaskService.updateSubtaskTimeSpent(TASK_ID, SUBTASK_ID, USER_ID, 15);

            assertThat(existingSubtask.getTimeSpent()).isEqualTo(20);
            assertThat(parentTask.getTimeSpent()).isEqualTo(65);
        }

        @Test
        void throwsWhenAdditionalTimeIsNull() {
            assertThatThrownBy(() -> subtaskService.updateSubtaskTimeSpent(TASK_ID, SUBTASK_ID, USER_ID, null))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(subtaskRepository, taskRepository);
        }

        @Test
        void throwsWhenAdditionalTimeIsNegative() {
            assertThatThrownBy(() -> subtaskService.updateSubtaskTimeSpent(TASK_ID, SUBTASK_ID, USER_ID, -1))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(subtaskRepository, taskRepository);
        }

        @Test
        void throwsSubtaskNotFoundWhenMissing() {
            when(subtaskRepository.findOwnedSubtask(TASK_ID, SUBTASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subtaskService.updateSubtaskTimeSpent(TASK_ID, SUBTASK_ID, USER_ID, 15))
                    .isInstanceOf(SubtaskNotFoundException.class);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateSubtask {

        @Test
        void updatesOnlyProvidedFieldsAndSaves() {
            UpdateSubtaskDto updateDto = new UpdateSubtaskDto();
            updateDto.setSubtaskName("Updated name");

            when(subtaskRepository.findOwnedSubtask(TASK_ID, SUBTASK_ID, USER_ID))
                    .thenReturn(Optional.of(existingSubtask));

            doAnswer(invocation -> {
                UpdateSubtaskDto source = invocation.getArgument(0);
                Subtask target = invocation.getArgument(1);
                Optional.ofNullable(source.getSubtaskName()).ifPresent(target::setSubtaskName);
                Optional.ofNullable(source.getDescription()).ifPresent(target::setDescription);
                Optional.ofNullable(source.getEndTime()).ifPresent(target::setEndTime);
                Optional.ofNullable(source.getTimeToSpend()).ifPresent(target::setTimeToSpend);
                Optional.ofNullable(source.getIsComplete()).ifPresent(target::setIsComplete);
                return null;
            }).when(subtaskMapper).updateSubtaskFromDto(any(UpdateSubtaskDto.class), any(Subtask.class));

            SubtaskDto expectedDto = new SubtaskDto();
            when(subtaskMapper.subtaskEntityToSubtaskDto(existingSubtask)).thenReturn(expectedDto);

            SubtaskDto result = subtaskService.updateSubtask(TASK_ID, SUBTASK_ID, USER_ID, updateDto);

            assertThat(existingSubtask.getSubtaskName()).isEqualTo("Updated name");
            assertThat(existingSubtask.getDescription()).isEqualTo("Original description");
            assertThat(result).isEqualTo(expectedDto);
        }

        @Test
        void throwsSubtaskNotFoundWhenTaskDoesNotOwnSubtask() {
            when(subtaskRepository.findOwnedSubtask(TASK_ID, SUBTASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subtaskService.updateSubtask(TASK_ID, SUBTASK_ID, USER_ID, new UpdateSubtaskDto()))
                    .isInstanceOf(SubtaskNotFoundException.class);

            verify(subtaskRepository, never()).save(any());
        }
    }

    @Nested
    class GetSubtasksByStatus {

        @Test
        void delegatesToActiveQuery() {
            when(subtaskRepository.findActiveOwnedSubtasks(TASK_ID, USER_ID)).thenReturn(List.of(existingSubtask));
            when(subtaskMapper.subtaskEntityToSubtaskDto(List.of(existingSubtask)))
                    .thenReturn(List.of(new SubtaskDto()));

            List<SubtaskDto> result = subtaskService.getSubtasksByStatus(TASK_ID, USER_ID, SubtaskStatusRequest.ACTIVE);

            assertThat(result).hasSize(1);
            verify(subtaskRepository).findActiveOwnedSubtasks(TASK_ID, USER_ID);
            verify(subtaskRepository, never()).findAllOwnedSubtasks(any(), any());
            verify(subtaskRepository, never()).findInactiveOwnedSubtasks(any(), any());
        }

        @Test
        void delegatesToAllQuery() {
            when(subtaskRepository.findAllOwnedSubtasks(TASK_ID, USER_ID)).thenReturn(List.of(existingSubtask));
            when(subtaskMapper.subtaskEntityToSubtaskDto(List.of(existingSubtask)))
                    .thenReturn(List.of(new SubtaskDto()));

            subtaskService.getSubtasksByStatus(TASK_ID, USER_ID, SubtaskStatusRequest.ALL);

            verify(subtaskRepository).findAllOwnedSubtasks(TASK_ID, USER_ID);
        }

        @Test
        void delegatesToInactiveQuery() {
            when(subtaskRepository.findInactiveOwnedSubtasks(TASK_ID, USER_ID)).thenReturn(List.of(existingSubtask));
            when(subtaskMapper.subtaskEntityToSubtaskDto(List.of(existingSubtask)))
                    .thenReturn(List.of(new SubtaskDto()));

            subtaskService.getSubtasksByStatus(TASK_ID, USER_ID, SubtaskStatusRequest.INACTIVE);

            verify(subtaskRepository).findInactiveOwnedSubtasks(TASK_ID, USER_ID);
        }
    }

    @Nested
    class DeleteSubtask {

        @Test
        void deletesWhenOwned() {
            when(subtaskRepository.findOwnedSubtask(TASK_ID, SUBTASK_ID, USER_ID))
                    .thenReturn(Optional.of(existingSubtask));

            subtaskService.deleteSubtask(SUBTASK_ID, TASK_ID, USER_ID);

            verify(subtaskRepository).delete(existingSubtask);
        }

        @Test
        void throwsAndDoesNotDeleteWhenNotOwned() {
            when(subtaskRepository.findOwnedSubtask(TASK_ID, SUBTASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subtaskService.deleteSubtask(SUBTASK_ID, TASK_ID, USER_ID))
                    .isInstanceOf(SubtaskNotFoundException.class);

            verify(subtaskRepository, never()).delete(any());
        }
    }

    @Nested
    class FindHelpers {

        @Test
        void findTaskOrThrowReturnsTask() {
            when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Optional.of(parentTask));

            Task result = subtaskService.findTaskOrThrow(TASK_ID, USER_ID);

            assertThat(result).isEqualTo(parentTask);
        }

        @Test
        void findOwnedSubtaskOrThrowThrowsWhenMissing() {
            when(subtaskRepository.findOwnedSubtask(TASK_ID, SUBTASK_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subtaskService.findOwnedSubtaskOrThrow(TASK_ID, SUBTASK_ID, USER_ID))
                    .isInstanceOf(SubtaskNotFoundException.class);
        }
    }
}