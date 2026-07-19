package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.subtasks WHERE t.userId = :userId ORDER BY t.isComplete ASC, t.endTime ASC")
    List<Task> findTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.subtasks WHERE t.userId = :userId AND t.isComplete = false ORDER BY t.endTime ASC")
    List<Task> findActiveTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.subtasks WHERE t.userId = :userId AND t.isComplete = true ORDER BY t.endTime ASC")
    List<Task> findInactiveTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.subtasks WHERE t.id = :taskId AND t.userId = :userId")
    Optional<Task> findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);

    boolean existsByIdAndUserId(Long taskId, Long userId);
}
