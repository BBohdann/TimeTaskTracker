package com.example.TaskService.integration;

import com.example.TaskService.controller.configuration.jwt.JwtAuthentication;
import com.example.TaskService.controller.request.subtask.CreateSubtaskRequest;
import com.example.TaskService.controller.request.subtask.UpdateSubtaskRequest;
import com.example.TaskService.controller.request.task.CreateTaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubtaskIntegrationTest extends BaseIntegrationTest {
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    private RequestPostProcessor asUser(Long userId) {
        return SecurityMockMvcRequestPostProcessors.authentication(
                new JwtAuthentication("test-user-" + userId, userId)
        );
    }

    @Test
    void createSubtask_returnsCreatedSubtaskWithIdAndTaskId() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);

        CreateSubtaskRequest request = new CreateSubtaskRequest();
        request.setSubtaskName("First subtask");
        request.setTimeToSpend(30);

        mockMvc.perform(post("/api/tasks/{taskId}/subtasks", taskId)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.createdTime").exists());
    }

    @Test
    void createSubtask_withBlankName_returnsBadRequest() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);

        CreateSubtaskRequest request = new CreateSubtaskRequest();
        request.setSubtaskName("   ");

        mockMvc.perform(post("/api/tasks/{taskId}/subtasks", taskId)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSubtask_whenParentTaskNotOwned_returnsNotFound() throws Exception {
        Long taskId = createTask(OTHER_USER_ID, "Someone else's task", 100);

        CreateSubtaskRequest request = new CreateSubtaskRequest();
        request.setSubtaskName("Trying to attach here");

        mockMvc.perform(post("/api/tasks/{taskId}/subtasks", taskId)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSubtask_whenParentTaskMissing_returnsNotFound() throws Exception {
        CreateSubtaskRequest request = new CreateSubtaskRequest();
        request.setSubtaskName("Orphan subtask attempt");

        mockMvc.perform(post("/api/tasks/{taskId}/subtasks", 999_999L)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSubtask_withoutAuthentication_returnsUnauthorized() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);

        CreateSubtaskRequest request = new CreateSubtaskRequest();
        request.setSubtaskName("Should not be created");

        mockMvc.perform(post("/api/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateSubtask_updatesFieldsAndReturnsThemUnderIdField() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        Long subtaskId = createSubtask(taskId, USER_ID, "Original name", 20);

        UpdateSubtaskRequest updateRequest = new UpdateSubtaskRequest();
        updateRequest.setSubtaskName("Updated name");
        updateRequest.setIsComplete(true);

        mockMvc.perform(patch("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subtaskId))
                .andExpect(jsonPath("$.subtaskId").doesNotExist())
                .andExpect(jsonPath("$.subtaskName").value("Updated name"))
                .andExpect(jsonPath("$.isComplete").value(true));
    }

    @Test
    void updateSubtask_leavesUnspecifiedFieldsUnchanged() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        Long subtaskId = createSubtask(taskId, USER_ID, "Keep my time", 25);

        UpdateSubtaskRequest updateRequest = new UpdateSubtaskRequest();
        updateRequest.setSubtaskName("Only name changes");

        mockMvc.perform(patch("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtaskName").value("Only name changes"))
                .andExpect(jsonPath("$.timeToSpend").value(25));
    }

    @Test
    void updateSubtask_whenNotOwned_returnsNotFound() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        Long subtaskId = createSubtask(taskId, USER_ID, "Private subtask", 20);

        UpdateSubtaskRequest updateRequest = new UpdateSubtaskRequest();
        updateRequest.setSubtaskName("Hijack attempt");

        mockMvc.perform(patch("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(OTHER_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSubtask_removesSubtaskAndDecrementsParentTimeToSpend() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        Long subtaskId = createSubtask(taskId, USER_ID, "Chunk of work", 30);

        mockMvc.perform(delete("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeToSpend").value(70));

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSubtask_withNullSubtaskTimeToSpend_leavesParentTimeToSpendUnchanged() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        CreateSubtaskRequest request = new CreateSubtaskRequest();
        request.setSubtaskName("No time estimate");
        Long subtaskId = createSubtaskFromRequest(taskId, USER_ID, request);

        mockMvc.perform(delete("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeToSpend").value(100));
    }

    @Test
    void deleteSubtask_clampsParentTimeToSpendAtZeroInsteadOfGoingNegative() throws Exception {
        Long taskId = createTask(USER_ID, "Small parent", 10);
        Long subtaskId = createSubtask(taskId, USER_ID, "Overweight subtask", 50);

        mockMvc.perform(delete("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeToSpend").value(1));
    }

    @Test
    void deleteSubtask_whenNotOwned_returnsNotFoundAndDoesNotDeleteOrDecrement() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        Long subtaskId = createSubtask(taskId, USER_ID, "Not yours", 30);

        mockMvc.perform(delete("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(OTHER_USER_ID)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeToSpend").value(100));
    }

    @Test
    void getSubtask_returnsSubtaskDetails() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        Long subtaskId = createSubtask(taskId, USER_ID, "Readable subtask", 15);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subtaskId))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.subtaskName").value("Readable subtask"));
    }

    @Test
    void getSubtask_whenBelongsToDifferentTask_returnsNotFound() throws Exception {
        Long taskAId = createTask(USER_ID, "Task A", 100);
        Long taskBId = createTask(USER_ID, "Task B", 100);
        Long subtaskUnderB = createSubtask(taskBId, USER_ID, "Belongs to B", 15);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks/{subtaskId}", taskAId, subtaskUnderB)
                        .with(asUser(USER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSubtask_whenMissing_returnsNotFound() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, 999_999L)
                        .with(asUser(USER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSubtasks_activeStatus_excludesCompletedSubtasks() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        Long activeSubtaskId = createSubtask(taskId, USER_ID, "Active subtask", 10);
        Long doneSubtaskId = createSubtask(taskId, USER_ID, "Done subtask", 10);
        markSubtaskComplete(taskId, doneSubtaskId, USER_ID);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks", taskId)
                        .param("status", "ACTIVE")
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(activeSubtaskId));
    }

    @Test
    void getSubtasks_inactiveStatus_returnsOnlyCompletedSubtasks() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        createSubtask(taskId, USER_ID, "Active subtask", 10);
        Long doneSubtaskId = createSubtask(taskId, USER_ID, "Done subtask", 10);
        markSubtaskComplete(taskId, doneSubtaskId, USER_ID);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks", taskId)
                        .param("status", "INACTIVE")
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(doneSubtaskId));
    }

    @Test
    void getSubtasks_allStatus_returnsEverythingUnderTask() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);
        createSubtask(taskId, USER_ID, "Subtask A", 10);
        createSubtask(taskId, USER_ID, "Subtask B", 10);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks", taskId)
                        .param("status", "ALL")
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getSubtasks_whenTaskNotOwned_returnsEmptyList() throws Exception {
        Long taskId = createTask(OTHER_USER_ID, "Someone else's task", 100);
        createSubtask(taskId, OTHER_USER_ID, "Not visible to USER_ID", 10);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks", taskId)
                        .param("status", "ALL")
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getSubtasks_withInvalidStatus_returnsBadRequest() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 100);

        mockMvc.perform(get("/api/tasks/{taskId}/subtasks", taskId)
                        .param("status", "NOT_A_REAL_STATUS")
                        .with(asUser(USER_ID)))
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

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long createSubtask(Long taskId, Long userId, String subtaskName, Integer timeToSpend) throws Exception {
        CreateSubtaskRequest request = new CreateSubtaskRequest();
        request.setSubtaskName(subtaskName);
        request.setTimeToSpend(timeToSpend);

        return createSubtaskFromRequest(taskId, userId, request);
    }

    private Long createSubtaskFromRequest(Long taskId, Long userId, CreateSubtaskRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/tasks/{taskId}/subtasks", taskId)
                        .with(asUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private void markSubtaskComplete(Long taskId, Long subtaskId, Long userId) throws Exception {
        UpdateSubtaskRequest updateRequest = new UpdateSubtaskRequest();
        updateRequest.setIsComplete(true);

        mockMvc.perform(patch("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }
}
