package com.example.TaskService.service.service;

import com.example.TaskService.controller.request.session.SessionStatusRequest;
import com.example.TaskService.controller.request.subtask.SubtaskStatusRequest;
import com.example.TaskService.data.entity.*;
import com.example.TaskService.data.repository.SubtaskRepository;
import com.example.TaskService.data.repository.TaskRepository;
import com.example.TaskService.data.repository.WorkSessionIntervalRepository;
import com.example.TaskService.data.repository.WorkSessionRepository;
import com.example.TaskService.service.dto.session.FinishSessionDto;
import com.example.TaskService.service.dto.session.StartSessionDto;
import com.example.TaskService.service.dto.session.WorkSessionDto;
import com.example.TaskService.service.dto.subtask.SubtaskDto;
import com.example.TaskService.service.exception.*;
import com.example.TaskService.service.mapper.WorkSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkSessionService {
    private final WorkSessionRepository workSessionRepository;
    private final WorkSessionIntervalRepository intervalRepository;
    private final TaskService taskService;
    private final SubtaskService subtaskService;
    private final WorkSessionMapper sessionMapper;

    @Transactional
    public WorkSessionDto startSession(StartSessionDto dto){
        validateTaskAccess(dto);
        validateSessionRules(dto);

        WorkSession session = sessionMapper.startSessionDtoToSessionEntity(dto);
        session.setStatus(WorkSessionStatus.RUNNING);

        workSessionRepository.save(session);
        startNewInterval(session);

        return sessionMapper.sessionEntityToSessionDto(session);
    }

    @Transactional
    public FinishSessionDto finishSession(Long sessionId, Long userId) {
        WorkSession session = getOwnedSessionOrThrow(sessionId, userId);
        requireStatus(session, WorkSessionStatus.RUNNING, WorkSessionStatus.PAUSED);


        closeActiveIntervalIfExists(sessionId);
        intervalRepository.flush();

        int totalDurationSeconds = intervalRepository.getTotalDuration(sessionId).intValue();
        session.setStatus(WorkSessionStatus.FINISHED);

        if(session.getSubtaskId() != null){
            subtaskService.updateSubtaskTimeSpent(
                    session.getTaskId(),
                    session.getSubtaskId(),
                    session.getUserId(),
                    totalDurationSeconds
            );
        }
        else {
            taskService.updateTimeSpent(
                    session.getTaskId(),
                    session.getUserId(),
                    totalDurationSeconds
            );
        }
        return sessionMapper.sessionEntityToFinishSessionDto(session, totalDurationSeconds);
    }

    @Transactional
    public void pauseSession(Long sessionId, Long userId) {
        WorkSession session = getOwnedSessionOrThrow(sessionId, userId);
        requireStatus(session, WorkSessionStatus.RUNNING);

        closeActiveIntervalOrThrow(sessionId);
        session.setStatus(WorkSessionStatus.PAUSED);
    }

    @Transactional
     public void resumeSession(Long sessionId, Long userId) {
        WorkSession session = getOwnedSessionOrThrow(sessionId, userId);
        requireStatus(session, WorkSessionStatus.PAUSED);

        startNewInterval(session);
        session.setStatus(WorkSessionStatus.RUNNING);
    }

    @Transactional(readOnly = true)
    public List<WorkSessionDto> getSessionsByStatus(Long userId, SessionStatusRequest statusFilter) {
        List<WorkSessionStatus> targetStatuses = switch (statusFilter) {
            case ACTIVE -> List.of(WorkSessionStatus.RUNNING, WorkSessionStatus.PAUSED);
            case INACTIVE -> List.of(WorkSessionStatus.FINISHED);
            case ALL -> List.of(WorkSessionStatus.RUNNING, WorkSessionStatus.PAUSED, WorkSessionStatus.FINISHED);
        };

        List<WorkSession> sessions = workSessionRepository.
                findOwnedSessionsByStatuses(
                        userId, targetStatuses
                );

        return sessionMapper.sessionEntityToSessionDto(sessions);
    }

    private void validateTaskAccess(StartSessionDto dto) {
        if (dto.getSubtaskId() != null) {
            subtaskService.assertSubtaskExists(dto.getSubtaskId(), dto.getTaskId(), dto.getUserId());
        } else {
            taskService.assertTaskExists(dto.getTaskId(), dto.getUserId());
        }
    }

    private void validateSessionRules(StartSessionDto dto) {
        if (workSessionRepository.countActiveSessions(dto.getUserId()) >= 5) {
            throw new SessionLimitException(5);
        }

        if (workSessionRepository.existsActiveSessionForTask(
                dto.getUserId(),
                dto.getTaskId()
        )) {
            throw new SessionAlreadyExistsException();
        }
    }

    private void requireStatus(WorkSession session, WorkSessionStatus... allowed) {
        for (WorkSessionStatus status : allowed) {
            if (session.getStatus() == status) {
                return;
            }
        }
        throw new SessionStatusException(session.getStatus());
    }

    private void closeActiveIntervalIfExists(Long sessionId) {
        intervalRepository.findActiveInterval(sessionId)
                .ifPresent(this::closeInterval);
    }

    private void closeActiveIntervalOrThrow(Long sessionId) {
        WorkSessionInterval interval = intervalRepository.findActiveInterval(sessionId)
                .orElseThrow(SessionIntervalNotFoundException::new);
        closeInterval(interval);
    }

    private void startNewInterval(WorkSession session) {
        WorkSessionInterval interval = new WorkSessionInterval();
        interval.setWorkSession(session);
        interval.setStartedAt(Instant.now());

        intervalRepository.save(interval);
    }

    private void closeInterval(WorkSessionInterval interval) {
        interval.setFinishedAt(Instant.now());
        interval.setDuration(Duration.between(
                interval.getStartedAt(),
                interval.getFinishedAt()).getSeconds()
        );
    }

    private WorkSession getOwnedSessionOrThrow(Long sessionId, Long userId) {
        return workSessionRepository.findOwnedSession(
                sessionId,
                userId
        ).orElseThrow(() ->
                new SessionNotFoundException(sessionId)
        );
    }
}
