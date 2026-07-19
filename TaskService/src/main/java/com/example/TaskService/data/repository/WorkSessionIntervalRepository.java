package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.WorkSessionInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSessionIntervalRepository extends JpaRepository<WorkSessionInterval, Long> {
    @Query("SELECT wsi FROM WorkSessionInterval wsi WHERE" +
            " wsi.workSession.id = :sessionId AND wsi.finishedAt IS NULL")
    Optional<WorkSessionInterval> findActiveInterval(
            @Param("sessionId") Long sessionId
    );

    List<WorkSessionInterval> findAllByWorkSessionId(
            Long sessionId
    );

    @Query("SELECT COALESCE(SUM(i.duration), 0) FROM" +
            " WorkSessionInterval i WHERE i.workSession.id = :sessionId")
    Long getTotalDuration(Long sessionId);
}
