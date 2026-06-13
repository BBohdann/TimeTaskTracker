package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.userId = :userId ORDER BY t.isComplete ASC")
    List<Task> findTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isComplete = false ORDER BY endTime ASC")
    List<Task> findActiveTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isComplete = true")
    List<Task> findInactiveTasksByUserId(@Param("userId") Long userId);

    Optional<Task> findByIdAndUserId(Long taskId, Long userId);
}
