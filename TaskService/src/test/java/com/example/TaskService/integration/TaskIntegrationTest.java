package com.example.TaskService.integration;

import com.example.TaskService.controller.configuration.jwt.JwtAuthentication;
import com.example.TaskService.controller.request.task.CreateTaskRequest;
import com.example.TaskService.controller.request.task.UpdateTaskRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskIntegrationTest extends BaseIntegrationTest {
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    private RequestPostProcessor asUser(Long userId) {
        return SecurityMockMvcRequestPostProcessors.authentication(
                new JwtAuthentication("test-user-" + userId, userId)
        );
    }

    @Test
    void createTask_returnsCreatedTaskWithIdAndTimestamp() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskName("Write integration tests");
        request.setDescription("Cover TaskController happy paths");
        request.setTimeToSpend(120);

        mockMvc.perform(post("/api/tasks")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdTime").exists());
    }

    @Test
    void createTask_withBlankTaskName_returnsBadRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskName("   ");

        mockMvc.perform(post("/api/tasks")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_withoutAuthentication_returnsUnauthorized() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskName("Should not be created");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateTask_updatesFieldsAndReturnsThem() throws Exception {
        Long taskId = createTask(USER_ID, "Original name", 60);

        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setTaskName("Updated name");
        updateRequest.setIsComplete(true);

        mockMvc.perform(patch("/api/tasks/{taskId}", taskId)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("Updated name"))
                .andExpect(jsonPath("$.isComplete").value(true));
    }

    @Test
    void updateTask_leavesUnspecifiedFieldsUnchanged() throws Exception {
        Long taskId = createTask(USER_ID, "Keep my description", 60);

        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setTaskName("Only name changes");

        mockMvc.perform(patch("/api/tasks/{taskId}", taskId)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("Only name changes"));
    }

    @Test
    void updateTask_whenOwnedByAnotherUser_returnsNotFound() throws Exception {
        Long taskId = createTask(USER_ID, "Private task", 30);

        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setTaskName("Hijack attempt");

        mockMvc.perform(patch("/api/tasks/{taskId}", taskId)
                        .with(asUser(OTHER_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_whenMissing_returnsNotFound() throws Exception {
        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setTaskName("Doesn't matter");

        mockMvc.perform(patch("/api/tasks/{taskId}", 999_999L)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_removesTaskPermanently() throws Exception {
        Long taskId = createTask(USER_ID, "To be deleted", 15);

        mockMvc.perform(delete("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_whenNotOwned_returnsNotFoundAndDoesNotDelete() throws Exception {
        Long taskId = createTask(USER_ID, "Not yours", 15);

        mockMvc.perform(delete("/api/tasks/{taskId}", taskId).with(asUser(OTHER_USER_ID)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk());
    }

    @Test
    void getTask_returnsTaskWithEmptySubtasksList() throws Exception {
        Long taskId = createTask(USER_ID, "Task with no subtasks yet", 45);

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.taskName").value("Task with no subtasks yet"))
                .andExpect(jsonPath("$.subtasks").isArray())
                .andExpect(jsonPath("$.subtasks").isEmpty());
    }

    @Test
    void getTask_whenMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/{taskId}", 999_999L).with(asUser(USER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasks_activeStatus_excludesCompletedTasks() throws Exception {
        Long activeTaskId = createTask(USER_ID, "Active task", 30);
        Long doneTaskId = createTask(USER_ID, "Done task", 30);
        markComplete(doneTaskId, USER_ID);

        mockMvc.perform(get("/api/tasks").param("status", "ACTIVE").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(activeTaskId));
    }

    @Test
    void getTasks_inactiveStatus_returnsOnlyCompletedTasks() throws Exception {
        createTask(USER_ID, "Active task", 30);
        Long doneTaskId = createTask(USER_ID, "Done task", 30);
        markComplete(doneTaskId, USER_ID);

        mockMvc.perform(get("/api/tasks").param("status", "INACTIVE").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(doneTaskId));
    }

    @Test
    void getTasks_allStatus_returnsEverythingOwnedByUser() throws Exception {
        createTask(USER_ID, "Task A", 30);
        createTask(USER_ID, "Task B", 30);

        mockMvc.perform(get("/api/tasks").param("status", "ALL").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTasks_neverReturnsAnotherUsersTasks() throws Exception {
        createTask(USER_ID, "Mine", 30);
        createTask(OTHER_USER_ID, "Not mine", 30);

        mockMvc.perform(get("/api/tasks").param("status", "ALL").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].taskName").value("Mine"));
    }

    @Test
    void getTasks_withInvalidStatus_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/tasks").param("status", "NOT_A_REAL_STATUS").with(asUser(USER_ID)))
                .andExpect(status().isBadRequest());
    }

    private Long createTask(Long userId, String taskName, Integer timeToSpend) throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskName(taskName);
        request.setTimeToSpend(timeToSpend);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .with(asUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private void markComplete(Long taskId, Long userId) throws Exception {
        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setIsComplete(true);

        mockMvc.perform(patch("/api/tasks/{taskId}", taskId)
                        .with(asUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }
}
