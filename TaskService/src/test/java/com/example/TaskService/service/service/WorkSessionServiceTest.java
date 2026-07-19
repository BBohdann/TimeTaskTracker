package com.example.TaskService.service.service;

import com.example.TaskService.controller.request.session.SessionStatusRequest;
import com.example.TaskService.data.entity.WorkSession;
import com.example.TaskService.data.entity.WorkSessionInterval;
import com.example.TaskService.data.entity.WorkSessionStatus;
import com.example.TaskService.data.repository.WorkSessionIntervalRepository;
import com.example.TaskService.data.repository.WorkSessionRepository;
import com.example.TaskService.service.dto.session.FinishSessionDto;
import com.example.TaskService.service.dto.session.StartSessionDto;
import com.example.TaskService.service.dto.session.WorkSessionDto;
import com.example.TaskService.service.exception.*;
import com.example.TaskService.service.mapper.WorkSessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkSessionServiceTest {
    @Mock
    private WorkSessionRepository workSessionRepository;
    @Mock
    private WorkSessionIntervalRepository intervalRepository;
    @Mock
    private TaskService taskService;
    @Mock
    private SubtaskService subtaskService;
    @Mock
    private WorkSessionMapper sessionMapper;

    @InjectMocks
    private WorkSessionService workSessionService;

    private static final Long USER_ID = 1L;
    private static final Long TASK_ID = 10L;
    private static final Long SUBTASK_ID = 20L;
    private static final Long SESSION_ID = 30L;

    @Nested
    class StartSession {

        @Test
        void startsSessionForTaskOnlyWhenNoSubtask() {
            StartSessionDto dto = new StartSessionDto();
            dto.setUserId(USER_ID);
            dto.setTaskId(TASK_ID);

            when(workSessionRepository.countActiveSessions(USER_ID)).thenReturn(0L);
            when(workSessionRepository.existsActiveSessionForTask(USER_ID, TASK_ID)).thenReturn(false);

            WorkSession mappedEntity = new WorkSession();
            when(sessionMapper.startSessionDtoToSessionEntity(dto)).thenReturn(mappedEntity);
            WorkSessionDto expectedDto = new WorkSessionDto();
            when(sessionMapper.sessionEntityToSessionDto(mappedEntity)).thenReturn(expectedDto);

            WorkSessionDto result = workSessionService.startSession(dto);

            assertThat(result).isEqualTo(expectedDto);
            assertThat(mappedEntity.getStatus()).isEqualTo(WorkSessionStatus.RUNNING);
            verify(subtaskService, never()).assertSubtaskExists(any(), any(), any());
            verify(workSessionRepository).save(mappedEntity);

            ArgumentCaptor<WorkSessionInterval> intervalCaptor = ArgumentCaptor.forClass(WorkSessionInterval.class);
            verify(intervalRepository).save(intervalCaptor.capture());
            assertThat(intervalCaptor.getValue().getWorkSession()).isEqualTo(mappedEntity);
            assertThat(intervalCaptor.getValue().getStartedAt()).isNotNull();
        }

        @Test
        void checksSubtaskOwnershipInsteadOfTaskWhenSubtaskIdPresent() {
            StartSessionDto dto = new StartSessionDto();
            dto.setUserId(USER_ID);
            dto.setTaskId(TASK_ID);
            dto.setSubtaskId(SUBTASK_ID);

            when(workSessionRepository.countActiveSessions(USER_ID)).thenReturn(0L);
            when(workSessionRepository.existsActiveSessionForTask(USER_ID, TASK_ID)).thenReturn(false);
            when(sessionMapper.startSessionDtoToSessionEntity(dto)).thenReturn(new WorkSession());
            when(workSessionRepository.save(any())).thenReturn(new WorkSession());
            when(sessionMapper.sessionEntityToSessionDto((WorkSession) any())).thenReturn(new WorkSessionDto());

            workSessionService.startSession(dto);

            verify(subtaskService).assertSubtaskExists(SUBTASK_ID, TASK_ID, USER_ID);
            verify(taskService, never()).assertTaskExists(any(), any());
        }

        @Test
        void throwsWhenSubtaskNotOwned() {
            StartSessionDto dto = new StartSessionDto();
            dto.setUserId(USER_ID);
            dto.setTaskId(TASK_ID);
            dto.setSubtaskId(SUBTASK_ID);

            doThrow(new SubtaskNotFoundException(SUBTASK_ID))
                    .when(subtaskService).assertSubtaskExists(SUBTASK_ID, TASK_ID, USER_ID);

            assertThatThrownBy(() -> workSessionService.startSession(dto))
                    .isInstanceOf(SubtaskNotFoundException.class);

            verifyNoInteractions(workSessionRepository);
        }

        @Test
        void throwsWhenTaskNotOwned() {
            StartSessionDto dto = new StartSessionDto();
            dto.setUserId(USER_ID);
            dto.setTaskId(TASK_ID);

            doThrow(new TaskNotFoundException(TASK_ID))
                    .when(taskService).assertTaskExists(TASK_ID, USER_ID);

            assertThatThrownBy(() -> workSessionService.startSession(dto))
                    .isInstanceOf(TaskNotFoundException.class);

            verifyNoInteractions(workSessionRepository);
        }

        @Test
        void throwsSessionLimitWhenFiveActiveSessionsAlready() {
            StartSessionDto dto = new StartSessionDto();
            dto.setUserId(USER_ID);
            dto.setTaskId(TASK_ID);

            when(workSessionRepository.countActiveSessions(USER_ID)).thenReturn(5L);

            assertThatThrownBy(() -> workSessionService.startSession(dto))
                    .isInstanceOf(SessionLimitException.class);

            verify(workSessionRepository, never()).save(any());
        }

        @Test
        void throwsSessionAlreadyExistsWhenTaskAlreadyTracked() {
            StartSessionDto dto = new StartSessionDto();
            dto.setUserId(USER_ID);
            dto.setTaskId(TASK_ID);

            when(workSessionRepository.countActiveSessions(USER_ID)).thenReturn(2L);
            when(workSessionRepository.existsActiveSessionForTask(USER_ID, TASK_ID)).thenReturn(true);

            assertThatThrownBy(() -> workSessionService.startSession(dto))
                    .isInstanceOf(SessionAlreadyExistsException.class);

            verify(workSessionRepository, never()).save(any());
        }
    }

    @Nested
    class FinishSession {

        private WorkSession runningSession;

        @BeforeEach
        void setUp() {
            runningSession = new WorkSession();
            runningSession.setId(SESSION_ID);
            runningSession.setUserId(USER_ID);
            runningSession.setTaskId(TASK_ID);
            runningSession.setStatus(WorkSessionStatus.RUNNING);
        }

        @Test
        void finishesTaskLevelSessionAndUpdatesTaskTimeSpent() {
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(runningSession));
            when(intervalRepository.findActiveInterval(SESSION_ID)).thenReturn(Optional.empty());
            when(intervalRepository.getTotalDuration(SESSION_ID)).thenReturn(120L);
            FinishSessionDto expectedDto = new FinishSessionDto();
            when(sessionMapper.sessionEntityToFinishSessionDto(runningSession, 120)).thenReturn(expectedDto);

            FinishSessionDto result = workSessionService.finishSession(SESSION_ID, USER_ID);

            assertThat(runningSession.getStatus()).isEqualTo(WorkSessionStatus.FINISHED);
            assertThat(result).isEqualTo(expectedDto);
            verify(taskService).updateTimeSpent(TASK_ID, USER_ID,120);
            verify(subtaskService, never()).updateSubtaskTimeSpent(any(), any(), any(), any());
        }

        @Test
        void finishesSubtaskLevelSessionAndUpdatesSubtaskTimeSpent() {
            runningSession.setSubtaskId(SUBTASK_ID);
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(runningSession));
            when(intervalRepository.findActiveInterval(SESSION_ID)).thenReturn(Optional.empty());
            when(intervalRepository.getTotalDuration(SESSION_ID)).thenReturn(90L);
            when(sessionMapper.sessionEntityToFinishSessionDto(runningSession, 90)).thenReturn(new FinishSessionDto());

            workSessionService.finishSession(SESSION_ID, USER_ID);

            verify(subtaskService).updateSubtaskTimeSpent(TASK_ID, SUBTASK_ID, USER_ID, 90);
            verify(taskService, never()).updateTimeSpent(any(), any(), any());
        }

        @Test
        void closesDanglingActiveIntervalBeforeFinishing() {
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(runningSession));
            WorkSessionInterval activeInterval = new WorkSessionInterval();
            activeInterval.setStartedAt(java.time.Instant.now().minusSeconds(30));
            when(intervalRepository.findActiveInterval(SESSION_ID)).thenReturn(Optional.of(activeInterval));
            when(intervalRepository.getTotalDuration(SESSION_ID)).thenReturn(30L);
            when(sessionMapper.sessionEntityToFinishSessionDto(any(), any())).thenReturn(new FinishSessionDto());

            workSessionService.finishSession(SESSION_ID, USER_ID);

            assertThat(activeInterval.getFinishedAt()).isNotNull();
            assertThat(activeInterval.getDuration()).isGreaterThan(0L);
            verify(intervalRepository).flush();
        }

        @Test
        void throwsWhenSessionAlreadyFinished() {
            runningSession.setStatus(WorkSessionStatus.FINISHED);
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(runningSession));

            assertThatThrownBy(() -> workSessionService.finishSession(SESSION_ID, USER_ID))
                    .isInstanceOf(SessionStatusException.class);

            verifyNoInteractions(intervalRepository);
        }

        @Test
        void throwsSessionNotFoundWhenNotOwned() {
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workSessionService.finishSession(SESSION_ID, USER_ID))
                    .isInstanceOf(SessionNotFoundException.class);
        }
    }

    @Nested
    class PauseSession {

        @Test
        void pausesRunningSessionAndClosesInterval() {
            WorkSession session = new WorkSession();
            session.setId(SESSION_ID);
            session.setStatus(WorkSessionStatus.RUNNING);
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(session));
            WorkSessionInterval activeInterval = new WorkSessionInterval();
            activeInterval.setStartedAt(java.time.Instant.now().minusSeconds(10));
            when(intervalRepository.findActiveInterval(SESSION_ID)).thenReturn(Optional.of(activeInterval));

            workSessionService.pauseSession(SESSION_ID, USER_ID);

            assertThat(session.getStatus()).isEqualTo(WorkSessionStatus.PAUSED);
            assertThat(activeInterval.getFinishedAt()).isNotNull();
        }

        @Test
        void throwsWhenSessionNotRunning() {
            WorkSession session = new WorkSession();
            session.setStatus(WorkSessionStatus.PAUSED);
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(session));

            assertThatThrownBy(() -> workSessionService.pauseSession(SESSION_ID, USER_ID))
                    .isInstanceOf(SessionStatusException.class);

            verify(intervalRepository, never()).findActiveInterval(any());
        }

        @Test
        void throwsSessionIntervalNotFoundWhenNoActiveIntervalToClose() {
            WorkSession session = new WorkSession();
            session.setStatus(WorkSessionStatus.RUNNING);
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(session));
            when(intervalRepository.findActiveInterval(SESSION_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workSessionService.pauseSession(SESSION_ID, USER_ID))
                    .isInstanceOf(SessionIntervalNotFoundException.class);

            verify(workSessionRepository, never()).save(any());
        }
    }

    @Nested
    class ResumeSession {

        @Test
        void resumesPausedSessionAndStartsNewInterval() {
            WorkSession session = new WorkSession();
            session.setStatus(WorkSessionStatus.PAUSED);
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(session));

            workSessionService.resumeSession(SESSION_ID, USER_ID);

            assertThat(session.getStatus()).isEqualTo(WorkSessionStatus.RUNNING);

            ArgumentCaptor<WorkSessionInterval> intervalCaptor = ArgumentCaptor.forClass(WorkSessionInterval.class);
            verify(intervalRepository).save(intervalCaptor.capture());
            assertThat(intervalCaptor.getValue().getWorkSession()).isEqualTo(session);
            assertThat(intervalCaptor.getValue().getStartedAt()).isNotNull();
        }

        @Test
        void throwsWhenSessionNotPaused() {
            WorkSession session = new WorkSession();
            session.setStatus(WorkSessionStatus.RUNNING);
            when(workSessionRepository.findOwnedSession(SESSION_ID, USER_ID)).thenReturn(Optional.of(session));

            assertThatThrownBy(() -> workSessionService.resumeSession(SESSION_ID, USER_ID))
                    .isInstanceOf(SessionStatusException.class);

            verifyNoInteractions(intervalRepository);
        }
    }

    @Nested
    class GetSessionsByStatus {

        @Test
        void mapsActiveFilterToRunningAndPaused() {
            when(workSessionRepository.findOwnedSessionsByStatuses(
                    eq(USER_ID),
                    eq(List.of(WorkSessionStatus.RUNNING, WorkSessionStatus.PAUSED))
            )).thenReturn(List.of());
            when(sessionMapper.sessionEntityToSessionDto(List.of())).thenReturn(List.of());

            workSessionService.getSessionsByStatus(USER_ID, SessionStatusRequest.ACTIVE);

            verify(workSessionRepository).findOwnedSessionsByStatuses(
                    USER_ID, List.of(WorkSessionStatus.RUNNING, WorkSessionStatus.PAUSED));
        }

        @Test
        void mapsInactiveFilterToFinishedOnly() {
            when(workSessionRepository.findOwnedSessionsByStatuses(eq(USER_ID), eq(List.of(WorkSessionStatus.FINISHED))))
                    .thenReturn(List.of());
            when(sessionMapper.sessionEntityToSessionDto(List.of())).thenReturn(List.of());

            workSessionService.getSessionsByStatus(USER_ID, SessionStatusRequest.INACTIVE);

            verify(workSessionRepository).findOwnedSessionsByStatuses(USER_ID, List.of(WorkSessionStatus.FINISHED));
        }
    }
}
