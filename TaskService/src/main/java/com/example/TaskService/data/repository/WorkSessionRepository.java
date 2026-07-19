package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.WorkSession;
import com.example.TaskService.data.entity.WorkSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {
    @Query("SELECT COUNT(w) FROM WorkSession w WHERE w.userId = :userId AND w.status IN ('RUNNING', 'PAUSED')")
    long countActiveSessions(@Param("userId") Long userId);

    @Query("SELECT COUNT(ws) > 0 FROM WorkSession ws " +
            "WHERE ws.userId = :userId AND ws.taskId = :taskId " +
            "AND ws.status IN ('RUNNING', 'PAUSED')")
    boolean existsActiveSessionForTask(
            @Param("userId") Long userId,
            @Param("taskId") Long taskId
    );

    @Query("SELECT ws FROM WorkSession ws WHERE ws.id = :sessionId AND ws.userId = :userId")
    Optional<WorkSession> findOwnedSession(
            @Param("sessionId") Long sessionId,
            @Param("userId") Long userId
    );

    @Query("SELECT ws FROM WorkSession ws WHERE ws.userId = :userId AND ws.status IN :statuses")
    List<WorkSession> findOwnedSessionsByStatuses(
            @Param("userId") Long userId,
            @Param("statuses") List<WorkSessionStatus> statuses
    );
}
