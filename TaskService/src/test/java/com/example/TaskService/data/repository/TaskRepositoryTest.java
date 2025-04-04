package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setUserId(1L);
        task.setTaskName("Test Task");
        task.setDescription("Test Description");
        task.setCreatedTime(LocalDateTime.now());
        task.setTimeToSpend(120);
        task.setTimeSpent(0);
        task.setIsComplete(false);

        taskRepository.save(task);
    }

    @Test
    void testFindTasksByUserId() {
        List<Task> tasks = taskRepository.findTasksByUserId(1L);
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getTaskName()).isEqualTo("Test Task");
    }

    @Test
    void testFindActiveTasksByUserId() {
        List<Task> tasks = taskRepository.findActiveTasksByUserId(1L);
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getIsComplete()).isFalse();
    }

    @Test
    void testExistsByTaskIdAndUserId() {
        boolean exists = taskRepository.existsByTaskIdAndUserId(task.getId(), task.getUserId());
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByIdAndUserId() {
        Optional<Task> foundTask = taskRepository.findByIdAndUserId(task.getId(), task.getUserId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTaskName()).isEqualTo("Test Task");
    }
}