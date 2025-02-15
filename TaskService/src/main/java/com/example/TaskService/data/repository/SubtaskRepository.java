package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    List<Subtask> findByTaskId(Long taskId);

    boolean existsByTaskId(Long taskId);

    boolean existsByIdAndTaskId(Long subtaskId, Long taskId);

    @Query("SELECT s.task.id FROM Subtask s WHERE s.id = :subtaskId")
    Optional<Long> findTaskIdBySubtaskId(@Param("subtaskId") Long subtaskId);

    @Modifying
    @Query("UPDATE Subtask s SET s.timeSpent = s.timeSpent + :timeSpent WHERE s.id = :id")
    void updateTimeSpent(@Param("id") Long id, @Param("timeSpent") Integer timeSpent);

    @Modifying
    @Query("UPDATE Subtask s SET s.isComplete = CASE WHEN s.isComplete = true THEN false ELSE true END WHERE s.id = :subtaskId")
    int changeIsCompleteFlag(@Param("subtaskId") Long subtaskId);

}
