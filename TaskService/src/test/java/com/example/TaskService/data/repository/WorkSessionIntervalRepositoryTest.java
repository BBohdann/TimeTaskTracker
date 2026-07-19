package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.WorkSession;
import com.example.TaskService.data.entity.WorkSessionInterval;
import com.example.TaskService.data.entity.WorkSessionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WorkSessionIntervalRepositoryTest {
    @Autowired
    private WorkSessionIntervalRepository intervalRepository;

    @Autowired
    private TestEntityManager entityManager;

    private WorkSession createWorkSession(Long userId, Long taskId, WorkSessionStatus status) {
        WorkSession session = new WorkSession();
        session.setUserId(userId);
        session.setTaskId(taskId);
        session.setStatus(status);
        return entityManager.persistAndFlush(session);
    }

    private WorkSessionInterval createInterval(WorkSession session, Instant startedAt, Instant finishedAt, Long duration) {
        WorkSessionInterval interval = new WorkSessionInterval();
        interval.setWorkSession(session);
        interval.setStartedAt(startedAt);
        interval.setFinishedAt(finishedAt);
        interval.setDuration(duration);
        return entityManager.persistAndFlush(interval);
    }

    @Test
    @DisplayName("findActiveInterval: returns the interval without finishedAt")
    void findActiveInterval_returnsUnfinishedInterval() {
        WorkSession session = createWorkSession(1L, 10L, WorkSessionStatus.RUNNING);
        createInterval(session, Instant.parse("2026-01-01T10:00:00Z"), Instant.parse("2026-01-01T10:30:00Z"), 1800L);
        WorkSessionInterval active = createInterval(session, Instant.parse("2026-01-01T11:00:00Z"), null, 0L);

        entityManager.clear();

        Optional<WorkSessionInterval> result = intervalRepository.findActiveInterval(session.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(active.getId());
        assertThat(result.get().getFinishedAt()).isNull();
    }

    @Test
    @DisplayName("findActiveInterval: returns an empty Optional if all intervals are completed")
    void findActiveInterval_allFinished_returnsEmpty() {
        WorkSession session = createWorkSession(1L, 10L, WorkSessionStatus.FINISHED);
        createInterval(session, Instant.parse("2026-01-01T10:00:00Z"), Instant.parse("2026-01-01T10:30:00Z"), 1800L);

        entityManager.clear();

        Optional<WorkSessionInterval> result = intervalRepository.findActiveInterval(session.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findActiveInterval: returns an empty Optional for a session without intervals")
    void findActiveInterval_noIntervals_returnsEmpty() {
        WorkSession session = createWorkSession(1L, 10L, WorkSessionStatus.RUNNING);
        entityManager.clear();

        Optional<WorkSessionInterval> result = intervalRepository.findActiveInterval(session.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getTotalDuration: sums the durations of all session intervals")
    void getTotalDuration_sumsAllIntervals() {
        WorkSession session = createWorkSession(1L, 10L, WorkSessionStatus.PAUSED);
        createInterval(session, Instant.parse("2026-01-01T10:00:00Z"), Instant.parse("2026-01-01T10:30:00Z"), 1800L);
        createInterval(session, Instant.parse("2026-01-01T11:00:00Z"), Instant.parse("2026-01-01T11:20:00Z"), 1200L);

        entityManager.clear();

        Long total = intervalRepository.getTotalDuration(session.getId());

        assertThat(total).isEqualTo(3000L);
    }

    @Test
    @DisplayName("getTotalDuration: returns 0 if the session has no intervals")
    void getTotalDuration_noIntervals_returnsZero() {
        WorkSession session = createWorkSession(1L, 10L, WorkSessionStatus.RUNNING);
        entityManager.clear();

        Long total = intervalRepository.getTotalDuration(session.getId());

        assertThat(total).isEqualTo(0L);
    }

    @Test
    @DisplayName("getTotalDuration: takes into account only the intervals of the specified session")
    void getTotalDuration_onlyCountsOwnSessionIntervals() {
        WorkSession session1 = createWorkSession(1L, 10L, WorkSessionStatus.RUNNING);
        WorkSession session2 = createWorkSession(1L, 11L, WorkSessionStatus.RUNNING);
        createInterval(session1, Instant.parse("2026-01-01T10:00:00Z"), Instant.parse("2026-01-01T10:30:00Z"), 1800L);
        createInterval(session2, Instant.parse("2026-01-01T10:00:00Z"), Instant.parse("2026-01-01T10:10:00Z"), 600L);

        entityManager.clear();

        Long total = intervalRepository.getTotalDuration(session1.getId());

        assertThat(total).isEqualTo(1800L);
    }
}
