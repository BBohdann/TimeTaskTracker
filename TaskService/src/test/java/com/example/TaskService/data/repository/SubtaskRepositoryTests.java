package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class SubtaskRepositoryTests {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Subtask subtask;
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setUserId(1L);
        task.setTaskName("Test Task");
        task.setCreatedTime(LocalDateTime.now());
        task = taskRepository.saveAndFlush(task);

        subtask = new Subtask();
        subtask.setSubtaskName("Test Subtask");
        subtask.setCreatedTime(LocalDateTime.now());
        subtask.setTask(task);
        subtask.setIsComplete(false);
        subtask = subtaskRepository.saveAndFlush(subtask);
    }

    @Test
    void testCreateSubtask() {
        subtaskRepository.save(subtask);

        Subtask savedSubtask = subtaskRepository.findById(subtask.getId()).orElseThrow();
        assertThat(savedSubtask.getSubtaskName()).isEqualTo("Test Subtask");
        assertThat(savedSubtask.getTask()).isEqualTo(task);
    }

    @Test
    void testFindBySubtaskId() {
        subtaskRepository.save(subtask);
        List<Subtask> subtasks = subtaskRepository.findByTaskId(task.getId());

        assertThat(subtasks).isNotEmpty();
        assertThat(subtasks.get(0).getTask().getId()).isEqualTo(task.getId());
    }

    @Test
    void testExistsByTaskId() {
        subtaskRepository.save(subtask);
        boolean exists = subtaskRepository.existsByTaskId(task.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByIdAndTaskId() {
        subtaskRepository.save(subtask);
        boolean exists = subtaskRepository.existsByIdAndTaskId(subtask.getId(), task.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void testFindTaskIdBySubtaskId() {
        subtaskRepository.save(subtask);
        Optional<Long> taskId = subtaskRepository.findTaskIdBySubtaskId(subtask.getId());

        assertThat(taskId).isPresent();
        assertThat(taskId.get()).isEqualTo(task.getId());
    }

    @Test
    void testChangeIsCompleteFlag() {
        subtaskRepository.save(subtask);

        int rowsUpdated = subtaskRepository.changeIsCompleteFlag(subtask.getId());
        Subtask updatedSubtask = subtaskRepository.findById(subtask.getId()).orElseThrow();

        assertThat(rowsUpdated).isEqualTo(1);
        assertThat(updatedSubtask.getIsComplete()).isTrue();
    }
}