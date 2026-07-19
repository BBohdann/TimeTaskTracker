package com.example.TaskService.integration;

import com.example.TaskService.controller.configuration.jwt.JwtAuthentication;
import com.example.TaskService.controller.request.session.StartSessionRequest;
import com.example.TaskService.controller.request.subtask.CreateSubtaskRequest;
import com.example.TaskService.controller.request.task.CreateTaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkSessionIntegrationTest extends BaseIntegrationTest {
 
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
 
    private RequestPostProcessor asUser(Long userId) {
        return SecurityMockMvcRequestPostProcessors.authentication(
                new JwtAuthentication("test-user-" + userId, userId)
        );
    }

    @Test
    void startSession_forTaskOnly_returnsCreatedSessionWithRunningStatus() throws Exception {
        Long taskId = createTask(USER_ID, "Task to track", 60);
 
        StartSessionRequest request = new StartSessionRequest();
        request.setTaskId(taskId);
 
        mockMvc.perform(post("/api/sessions")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.subtaskId").doesNotExist())
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }
 
    @Test
    void startSession_forSubtask_returnsCreatedSessionWithSubtaskId() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 60);
        Long subtaskId = createSubtask(taskId, USER_ID, "Tracked subtask", 20);
 
        StartSessionRequest request = new StartSessionRequest();
        request.setTaskId(taskId);
        request.setSubtaskId(subtaskId);
 
        mockMvc.perform(post("/api/sessions")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.subtaskId").value(subtaskId))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }
 
    @Test
    void startSession_withoutTaskId_returnsBadRequest() throws Exception {
        StartSessionRequest request = new StartSessionRequest();
 
        mockMvc.perform(post("/api/sessions")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void startSession_whenTaskNotOwned_returnsNotFound() throws Exception {
        Long taskId = createTask(OTHER_USER_ID, "Someone else's task", 60);
 
        StartSessionRequest request = new StartSessionRequest();
        request.setTaskId(taskId);
 
        mockMvc.perform(post("/api/sessions")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void startSession_whenSubtaskNotOwned_returnsNotFound() throws Exception {
        Long taskId = createTask(OTHER_USER_ID, "Someone else's task", 60);
        Long subtaskId = createSubtask(taskId, OTHER_USER_ID, "Someone else's subtask", 20);
 
        StartSessionRequest request = new StartSessionRequest();
        request.setTaskId(taskId);
        request.setSubtaskId(subtaskId);
 
        mockMvc.perform(post("/api/sessions")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void startSession_whenTaskAlreadyHasActiveSession_returnsBadRequest() throws Exception {
        Long taskId = createTask(USER_ID, "Task with a running timer", 60);
        startSession(USER_ID, taskId, null);
 
        StartSessionRequest secondAttempt = new StartSessionRequest();
        secondAttempt.setTaskId(taskId);
 
        mockMvc.perform(post("/api/sessions")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondAttempt)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void startSession_whenFiveActiveSessionsAlreadyExist_returnsBadRequest() throws Exception {
        for (int i = 0; i < 5; i++) {
            Long taskId = createTask(USER_ID, "Task #" + i, 60);
            startSession(USER_ID, taskId, null);
        }
 
        Long sixthTaskId = createTask(USER_ID, "One task too many", 60);
        StartSessionRequest sixthAttempt = new StartSessionRequest();
        sixthAttempt.setTaskId(sixthTaskId);
 
        mockMvc.perform(post("/api/sessions")
                        .with(asUser(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sixthAttempt)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void startSession_withoutAuthentication_returnsUnauthorized() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        StartSessionRequest request = new StartSessionRequest();
        request.setTaskId(taskId);
 
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void finishSession_forTaskLevelSession_finishesAndIncrementsTaskTimeSpent() throws Exception {
        Long taskId = createTask(USER_ID, "Task to finish", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        MvcResult result = mockMvc.perform(post("/api/sessions/{sessionId}/finish", sessionId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.duration").isNumber())
                .andReturn();
 
        int reportedDuration = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("duration").asInt();

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeSpent").value(reportedDuration));
    }
 
    @Test
    void finishSession_forSubtaskLevelSession_incrementsSubtaskNotTaskTimeSpent() throws Exception {
        Long taskId = createTask(USER_ID, "Parent task", 60);
        Long subtaskId = createSubtask(taskId, USER_ID, "Tracked subtask", 20);
        Long sessionId = startSession(USER_ID, taskId, subtaskId);
 
        MvcResult result = mockMvc.perform(post("/api/sessions/{sessionId}/finish", sessionId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtaskId").value(subtaskId))
                .andReturn();
 
        int reportedDuration = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("duration").asInt();
 
        mockMvc.perform(get("/api/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeSpent").value(reportedDuration));

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeSpent").value(0));
    }
 
    @Test
    void finishSession_closesDanglingActiveIntervalBeforeFinishing() throws Exception {
        Long taskId = createTask(USER_ID, "Task with pause/resume history", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/sessions/{sessionId}/resume", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());
 
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"));
    }
 
    @Test
    void finishSession_whenAlreadyFinished_returnsBadRequest() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isOk());
 
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void finishSession_whenNotOwned_returnsNotFound() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", sessionId).with(asUser(OTHER_USER_ID)))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void finishSession_whenMissing_returnsNotFound() throws Exception {
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", 999_999L).with(asUser(USER_ID)))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void pauseSession_transitionsRunningToPaused() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());
 
        mockMvc.perform(get("/api/sessions").param("status", "ACTIVE").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PAUSED"));
    }
 
    @Test
    void pauseSession_whenAlreadyPaused_returnsBadRequest() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());
 
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void pauseSession_whenFinished_returnsBadRequest() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isOk());
 
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void pauseSession_whenNotOwned_returnsNotFound() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", sessionId).with(asUser(OTHER_USER_ID)))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void resumeSession_transitionsPausedToRunning() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/sessions/{sessionId}/resume", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());
 
        mockMvc.perform(get("/api/sessions").param("status", "ACTIVE").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("RUNNING"));
    }
 
    @Test
    void resumeSession_whenAlreadyRunning_returnsBadRequest() throws Exception {
        Long taskId = createTask(USER_ID, "Task", 60);
        Long sessionId = startSession(USER_ID, taskId, null);
 
        mockMvc.perform(post("/api/sessions/{sessionId}/resume", sessionId).with(asUser(USER_ID)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void getUserSessions_defaultStatus_returnsAllSessionsRegardlessOfState() throws Exception {
        Long taskA = createTask(USER_ID, "Task A", 60);
        Long taskB = createTask(USER_ID, "Task B", 60);
        Long runningSessionId = startSession(USER_ID, taskA, null);
        Long finishedSessionId = startSession(USER_ID, taskB, null);
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", finishedSessionId).with(asUser(USER_ID)))
                .andExpect(status().isOk());
 
        mockMvc.perform(get("/api/sessions").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
 
    @Test
    void getUserSessions_activeStatus_returnsRunningAndPausedOnly() throws Exception {
        Long taskA = createTask(USER_ID, "Task A", 60);
        Long taskB = createTask(USER_ID, "Task B", 60);
        Long runningSessionId = startSession(USER_ID, taskA, null);
        Long pausedSessionId = startSession(USER_ID, taskB, null);
        mockMvc.perform(post("/api/sessions/{sessionId}/pause", pausedSessionId).with(asUser(USER_ID)))
                .andExpect(status().isNoContent());
 
        Long taskC = createTask(USER_ID, "Task C", 60);
        Long finishedSessionId = startSession(USER_ID, taskC, null);
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", finishedSessionId).with(asUser(USER_ID)))
                .andExpect(status().isOk());
 
        mockMvc.perform(get("/api/sessions").param("status", "ACTIVE").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUserSessions_inactiveStatus_returnsOnlyFinishedSessions() throws Exception {
        Long taskA = createTask(USER_ID, "Task A", 60);
        Long taskB = createTask(USER_ID, "Task B", 60);
        Long finishedSessionId = startSession(USER_ID, taskA, null);
        startSession(USER_ID, taskB, null);
        mockMvc.perform(post("/api/sessions/{sessionId}/finish", finishedSessionId).with(asUser(USER_ID)))
                .andExpect(status().isOk());
 
        mockMvc.perform(get("/api/sessions").param("status", "INACTIVE").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(finishedSessionId));
    }
 
    @Test
    void getUserSessions_neverReturnsAnotherUsersSessions() throws Exception {
        Long myTask = createTask(USER_ID, "Mine", 60);
        Long otherTask = createTask(OTHER_USER_ID, "Not mine", 60);
        startSession(USER_ID, myTask, null);
        startSession(OTHER_USER_ID, otherTask, null);
 
        mockMvc.perform(get("/api/sessions").param("status", "ALL").with(asUser(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].taskId").value(myTask));
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
 
        MvcResult result = mockMvc.perform(post("/api/tasks/{taskId}/subtasks", taskId)
                        .with(asUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
 
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
 
    private Long startSession(Long userId, Long taskId, Long subtaskId) throws Exception {
        StartSessionRequest request = new StartSessionRequest();
        request.setTaskId(taskId);
        request.setSubtaskId(subtaskId);
 
        MvcResult result = mockMvc.perform(post("/api/sessions")
                        .with(asUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
 
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}