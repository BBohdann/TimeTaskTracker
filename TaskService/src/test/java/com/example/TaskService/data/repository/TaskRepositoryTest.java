package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    private Task createTask(Long userId, String name, boolean complete, Instant endTime) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTaskName(name);
        task.setTimeToSpend(60);
        task.setIsComplete(complete);
        task.setEndTime(endTime);
        return entityManager.persistAndFlush(task);
    }

    private Subtask createSubtask(Task task, String name) {
        Subtask subtask = new Subtask();
        subtask.setTask(task);
        subtask.setSubtaskName(name);
        subtask.setTimeToSpend(30);
        return entityManager.persistAndFlush(subtask);
    }

    @Test
    @DisplayName("findTasksByUserId: sorts incomplete items first, then by endTime, and loads subtasks")
    void findTasksByUserId_returnsSortedTasksWithSubtasks() {
        Task t1 = createTask(USER_ID, "Task A", true, Instant.parse("2026-01-10T00:00:00Z"));
        Task t2 = createTask(USER_ID, "Task B", false, Instant.parse("2026-01-05T00:00:00Z"));
        Task t3 = createTask(USER_ID, "Task C", false, Instant.parse("2026-01-01T00:00:00Z"));
        createTask(OTHER_USER_ID, "Someone else's task", false, Instant.now());

        createSubtask(t2, "Subtask B1");

        entityManager.clear();

        List<Task> result = taskRepository.findTasksByUserId(USER_ID);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(t3.getId());
        assertThat(result.get(1).getId()).isEqualTo(t2.getId());
        assertThat(result.get(2).getId()).isEqualTo(t1.getId());
        assertThat(result.get(1).getSubtasks()).hasSize(1);
    }

    @Test
    @DisplayName("findActiveTasksByUserId: returns only incomplete tasks without duplicates via JOIN FETCH")
    void findActiveTasksByUserId_returnsOnlyIncompleteWithoutDuplicates() {
        Task t1 = createTask(USER_ID, "Active 1", false, Instant.parse("2026-02-01T00:00:00Z"));
        Task t2 = createTask(USER_ID, "Active 2", false, Instant.parse("2026-01-01T00:00:00Z"));
        createTask(USER_ID, "Done", true, Instant.now());

        createSubtask(t1, "Sub 1");
        createSubtask(t1, "Sub 2");

        entityManager.clear();

        List<Task> result = taskRepository.findActiveTasksByUserId(USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getId).containsExactly(t2.getId(), t1.getId());
        assertThat(result).noneMatch(Task::getIsComplete);
        assertThat(result.stream().filter(t -> t.getId().equals(t1.getId())).count()).isEqualTo(1);
    }

    @Test
    @DisplayName("findInactiveTasksByUserId: returns only completed tasks, sorted by endTime")
    void findInactiveTasksByUserId_returnsOnlyCompleted() {
        Task t1 = createTask(USER_ID, "Done 1", true, Instant.parse("2026-02-01T00:00:00Z"));
        Task t2 = createTask(USER_ID, "Done 2", true, Instant.parse("2026-01-01T00:00:00Z"));
        createTask(USER_ID, "Active", false, Instant.now());

        entityManager.clear();

        List<Task> result = taskRepository.findInactiveTasksByUserId(USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getId).containsExactly(t2.getId(), t1.getId());
        assertThat(result).allMatch(Task::getIsComplete);
    }

    @Test
    @DisplayName("findByIdAndUserId: returns the task if it belongs to the user")
    void findByIdAndUserId_ownedTask_returnsTask() {
        Task task = createTask(USER_ID, "Mine", false, Instant.now());
        entityManager.clear();

        Optional<Task> result = taskRepository.findByIdAndUserId(task.getId(), USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getTaskName()).isEqualTo("Mine");
    }

    @Test
    @DisplayName("findByIdAndUserId: returns an empty Optional if the task belongs to another user")
    void findByIdAndUserId_notOwned_returnsEmpty() {
        Task task = createTask(OTHER_USER_ID, "Not mine", false, Instant.now());
        entityManager.clear();

        Optional<Task> result = taskRepository.findByIdAndUserId(task.getId(), USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndUserId: returns an empty Optional for a non-existent id")
    void findByIdAndUserId_nonExistingId_returnsEmpty() {
        Optional<Task> result = taskRepository.findByIdAndUserId(999L, USER_ID);

        assertThat(result).isEmpty();
    }
}