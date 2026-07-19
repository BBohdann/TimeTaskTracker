package com.example.TaskService.data.repository;

import com.example.TaskService.data.entity.Subtask;
import com.example.TaskService.data.entity.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SubtaskRepositoryTest {
    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    private Task createTask(Long userId, String name) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTaskName(name);
        task.setTimeToSpend(60);
        task.setIsComplete(false);
        return entityManager.persistAndFlush(task);
    }

    private Subtask createSubtask(Task task, String name, boolean complete, Instant endTime) {
        Subtask subtask = new Subtask();
        subtask.setTask(task);
        subtask.setSubtaskName(name);
        subtask.setTimeToSpend(30);
        subtask.setIsComplete(complete);
        subtask.setEndTime(endTime);
        return entityManager.persistAndFlush(subtask);
    }

    @Test
    @DisplayName("findOwnedSubtask: returns the subtask if the task and user match")
    void findOwnedSubtask_owned_returnsSubtask() {
        Task task = createTask(USER_ID, "Task");
        Subtask subtask = createSubtask(task, "Sub", false, Instant.now());

        entityManager.clear();

        Optional<Subtask> result = subtaskRepository.findOwnedSubtask(task.getId(), subtask.getId(), USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(subtask.getId());
        assertThat(result.get().getTask().getId()).isEqualTo(task.getId());
    }

    @Test
    @DisplayName("findOwnedSubtask: empty Optional if the task belongs to another user")
    void findOwnedSubtask_wrongUser_returnsEmpty() {
        Task task = createTask(OTHER_USER_ID, "Task");
        Subtask subtask = createSubtask(task, "Sub", false, Instant.now());

        entityManager.clear();

        Optional<Subtask> result = subtaskRepository.findOwnedSubtask(task.getId(), subtask.getId(), USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findOwnedSubtask: an empty Optional if the provided taskId does not correspond to an actual subtask.")
    void findOwnedSubtask_wrongTaskId_returnsEmpty() {
        Task task = createTask(USER_ID, "Task");
        Task otherTask = createTask(USER_ID, "Other Task");
        Subtask subtask = createSubtask(task, "Sub", false, Instant.now());

        entityManager.clear();

        Optional<Subtask> result = subtaskRepository.findOwnedSubtask(otherTask.getId(), subtask.getId(), USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findOwnedSubtask: empty Optional for a non-existent subtaskId")
    void findOwnedSubtask_nonExistingSubtask_returnsEmpty() {
        Task task = createTask(USER_ID, "Task");

        entityManager.clear();

        Optional<Subtask> result = subtaskRepository.findOwnedSubtask(task.getId(), 999L, USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllOwnedSubtasks: sorts by incomplete items first, then by endTime")
    void findAllOwnedSubtasks_returnsSorted() {
        Task task = createTask(USER_ID, "Task");
        Subtask s1 = createSubtask(task, "Done", true, Instant.parse("2026-01-10T00:00:00Z"));
        Subtask s2 = createSubtask(task, "Active early", false, Instant.parse("2026-01-01T00:00:00Z"));
        Subtask s3 = createSubtask(task, "Active late", false, Instant.parse("2026-01-05T00:00:00Z"));

        entityManager.clear();

        List<Subtask> result = subtaskRepository.findAllOwnedSubtasks(task.getId(), USER_ID);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Subtask::getId)
                .containsExactly(s2.getId(), s3.getId(), s1.getId());
    }

    @Test
    @DisplayName("findAllOwnedSubtasks: does not return subtasks of other users' tasks")
    void findAllOwnedSubtasks_excludesOtherUsersSubtasks() {
        Task ownTask = createTask(USER_ID, "Own");
        Task foreignTask = createTask(OTHER_USER_ID, "Foreign");
        Subtask ownSub = createSubtask(ownTask, "Own sub", false, Instant.now());
        createSubtask(foreignTask, "Foreign sub", false, Instant.now());

        entityManager.clear();

        List<Subtask> result = subtaskRepository.findAllOwnedSubtasks(ownTask.getId(), USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ownSub.getId());
    }

    @Test
    @DisplayName("findActiveOwnedSubtasks: returns only incomplete subtasks, sorted by endTime")
    void findActiveOwnedSubtasks_returnsOnlyIncomplete() {
        Task task = createTask(USER_ID, "Task");
        Subtask s1 = createSubtask(task, "Active 1", false, Instant.parse("2026-02-01T00:00:00Z"));
        Subtask s2 = createSubtask(task, "Active 2", false, Instant.parse("2026-01-01T00:00:00Z"));
        createSubtask(task, "Done", true, Instant.now());

        entityManager.clear();

        List<Subtask> result = subtaskRepository.findActiveOwnedSubtasks(task.getId(), USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Subtask::getId).containsExactly(s2.getId(), s1.getId());
        assertThat(result).noneMatch(Subtask::getIsComplete);
    }

    @Test
    @DisplayName("findActiveOwnedSubtasks: an empty list if all subtasks are completed")
    void findActiveOwnedSubtasks_allCompleted_returnsEmptyList() {
        Task task = createTask(USER_ID, "Task");
        createSubtask(task, "Done", true, Instant.now());

        entityManager.clear();

        List<Subtask> result = subtaskRepository.findActiveOwnedSubtasks(task.getId(), USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findInactiveOwnedSubtasks: returns only completed subtasks, sorted by endTime")
    void findInactiveOwnedSubtasks_returnsOnlyCompleted() {
        Task task = createTask(USER_ID, "Task");
        Subtask s1 = createSubtask(task, "Done 1", true, Instant.parse("2026-02-01T00:00:00Z"));
        Subtask s2 = createSubtask(task, "Done 2", true, Instant.parse("2026-01-01T00:00:00Z"));
        createSubtask(task, "Active", false, Instant.now());

        entityManager.clear();

        List<Subtask> result = subtaskRepository.findInactiveOwnedSubtasks(task.getId(), USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Subtask::getId).containsExactly(s2.getId(), s1.getId());
        assertThat(result).allMatch(Subtask::getIsComplete);
    }

    @Test
    @DisplayName("findInactiveOwnedSubtasks: an empty list if there are no completed subtasks")
    void findInactiveOwnedSubtasks_noneCompleted_returnsEmptyList() {
        Task task = createTask(USER_ID, "Task");
        createSubtask(task, "Active", false, Instant.now());

        entityManager.clear();

        List<Subtask> result = subtaskRepository.findInactiveOwnedSubtasks(task.getId(), USER_ID);

        assertThat(result).isEmpty();
    }
}