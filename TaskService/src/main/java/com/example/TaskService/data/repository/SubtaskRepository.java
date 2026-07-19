package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    @Query("SELECT s FROM Subtask s JOIN FETCH s.task WHERE s.id = :subtaskId AND s.task.id = :taskId AND s.task.userId = :userId")
    Optional<Subtask> findOwnedSubtask(@Param("taskId") Long taskId,
                                       @Param("subtaskId") Long subtaskId,
                                       @Param("userId") Long userId
    );

    @Query("SELECT s FROM Subtask s WHERE s.task.id = :taskId AND s.task.userId = :userId ORDER BY s.isComplete ASC, s.endTime ASC")
    List<Subtask> findAllOwnedSubtasks(
                                       @Param("taskId") Long taskId,
                                       @Param("userId") Long userId
    );

    @Query("SELECT s FROM Subtask s WHERE s.task.id = :taskId AND s.task.userId = :userId AND s.isComplete = false ORDER BY s.endTime ASC")
    List<Subtask> findActiveOwnedSubtasks(
                                          @Param("taskId") Long taskId,
                                          @Param("userId") Long userId
    );

    @Query("SELECT s FROM Subtask s WHERE s.task.id = :taskId AND s.task.userId = :userId AND s.isComplete = true ORDER BY s.endTime ASC")
    List<Subtask> findInactiveOwnedSubtasks(
                                          @Param("taskId") Long taskId,
                                          @Param("userId") Long userId
    );

    boolean existsByIdAndTaskIdAndTaskUserId(
            Long subtaskId,
            Long taskId,
            Long userId
    );
}
