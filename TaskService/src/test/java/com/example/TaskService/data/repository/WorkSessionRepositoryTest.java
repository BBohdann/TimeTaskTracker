package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.WorkSession;
import com.example.TaskService.data.entity.WorkSessionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WorkSessionRepositoryTest {
    @Autowired
    private WorkSessionRepository workSessionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    private WorkSession createSession(Long userId, Long taskId, WorkSessionStatus status) {
        WorkSession session = new WorkSession();
        session.setUserId(userId);
        session.setTaskId(taskId);
        session.setStatus(status);
        return entityManager.persistAndFlush(session);
    }

    @Test
    @DisplayName("countActiveSessions: takes into account only sessions with RUNNING and PAUSED statuses")
    void countActiveSessions_countsRunningAndPausedOnly() {
        createSession(USER_ID, 1L, WorkSessionStatus.RUNNING);
        createSession(USER_ID, 2L, WorkSessionStatus.PAUSED);
        createSession(USER_ID, 3L, WorkSessionStatus.FINISHED);
        createSession(OTHER_USER_ID, 4L, WorkSessionStatus.RUNNING);

        entityManager.clear();

        long count = workSessionRepository.countActiveSessions(USER_ID);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("countActiveSessions: returns 0 if there are no active sessions")
    void countActiveSessions_noActiveSessions_returnsZero() {
        createSession(USER_ID, 1L, WorkSessionStatus.FINISHED);

        entityManager.clear();

        long count = workSessionRepository.countActiveSessions(USER_ID);

        assertThat(count).isZero();
    }

    @Test
    @DisplayName("existsActiveSessionForTask: true if there is an active session for the task")
    void existsActiveSessionForTask_activeExists_returnsTrue() {
        createSession(USER_ID, 100L, WorkSessionStatus.RUNNING);

        entityManager.clear();

        boolean exists = workSessionRepository.existsActiveSessionForTask(USER_ID, 100L);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsActiveSessionForTask: false if the session for the task has already ended")
    void existsActiveSessionForTask_onlyFinished_returnsFalse() {
        createSession(USER_ID, 100L, WorkSessionStatus.FINISHED);

        entityManager.clear();

        boolean exists = workSessionRepository.existsActiveSessionForTask(USER_ID, 100L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsActiveSessionForTask: false for another user's active session")
    void existsActiveSessionForTask_differentUser_returnsFalse() {
        createSession(OTHER_USER_ID, 100L, WorkSessionStatus.RUNNING);

        entityManager.clear();

        boolean exists = workSessionRepository.existsActiveSessionForTask(USER_ID, 100L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findOwnedSession: returns the session if it belongs to the user")
    void findOwnedSession_owned_returnsSession() {
        WorkSession session = createSession(USER_ID, 100L, WorkSessionStatus.RUNNING);

        entityManager.clear();

        Optional<WorkSession> result = workSessionRepository.findOwnedSession(session.getId(), USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(session.getId());
    }

    @Test
    @DisplayName("findOwnedSession: returns an empty Optional if the session belongs to another user")
    void findOwnedSession_notOwned_returnsEmpty() {
        WorkSession session = createSession(OTHER_USER_ID, 100L, WorkSessionStatus.RUNNING);

        entityManager.clear();

        Optional<WorkSession> result = workSessionRepository.findOwnedSession(session.getId(), USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findOwnedSessionsByStatuses: returns only sessions with the provided statuses")
    void findOwnedSessionsByStatuses_filtersCorrectly() {
        WorkSession running = createSession(USER_ID, 1L, WorkSessionStatus.RUNNING);
        WorkSession paused = createSession(USER_ID, 2L, WorkSessionStatus.PAUSED);
        createSession(USER_ID, 3L, WorkSessionStatus.FINISHED);
        createSession(OTHER_USER_ID, 4L, WorkSessionStatus.RUNNING);

        entityManager.clear();

        List<WorkSession> result = workSessionRepository.findOwnedSessionsByStatuses(
                USER_ID, List.of(WorkSessionStatus.RUNNING, WorkSessionStatus.PAUSED));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(WorkSession::getId)
                .containsExactlyInAnyOrder(running.getId(), paused.getId());
    }

    @Test
    @DisplayName("findOwnedSessionsByStatuses: returns an empty list if no session matches")
    void findOwnedSessionsByStatuses_noMatches_returnsEmptyList() {
        createSession(USER_ID, 1L, WorkSessionStatus.FINISHED);

        entityManager.clear();

        List<WorkSession> result = workSessionRepository.findOwnedSessionsByStatuses(
                USER_ID, List.of(WorkSessionStatus.RUNNING));

        assertThat(result).isEmpty();
    }
}
