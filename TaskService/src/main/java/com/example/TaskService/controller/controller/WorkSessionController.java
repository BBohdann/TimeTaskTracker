package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.configuration.mvc.CurrentUserId;
import com.example.TaskService.controller.request.session.SessionStatusRequest;
import com.example.TaskService.controller.request.session.StartSessionRequest;
import com.example.TaskService.controller.response.session.FinishSessionResponse;
import com.example.TaskService.controller.response.session.SessionResponse;
import com.example.TaskService.controller.response.session.StartSessionResponse;
import com.example.TaskService.service.dto.session.StartSessionDto;
import com.example.TaskService.service.mapper.WorkSessionMapper;
import com.example.TaskService.service.service.WorkSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Timer Management", description = "Core API for tracking real-time user activity via sessions. Handles status limits and interval calculations.")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("isAuthenticated()")
public class WorkSessionController {
    private final WorkSessionService workSessionService;
    private final WorkSessionMapper sessionMapper;

    @Operation(summary = "Get user work sessions filtered by status",
            description = "Retrieves work sessions. Filter parameter accepts: ALL, ACTIVE (RUNNING + PAUSED), INACTIVE (FINISHED).")
    @GetMapping
    public ResponseEntity<List<SessionResponse>> getUserSessions(
            @RequestParam(defaultValue = "ALL") SessionStatusRequest status,
            @CurrentUserId Long userId) {
        List<SessionResponse> sessionResponse = sessionMapper.sessionDtoToSessionResponse(
                workSessionService.getSessionsByStatus(userId, status)
        );
        return ResponseEntity.ok(sessionResponse);
    }

    @Operation(summary = "Start a new work session",
            description = "Initializes a session and opens the first time-tracking interval. Limits: Max 5 active sessions per user; task cannot have parallel active sessions.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Session successfully spawned"),
            @ApiResponse(responseCode = "400", description = "Session rule violation (e.g. limit reached or duplicate task tracker)")
    })
    @PostMapping
    public ResponseEntity<StartSessionResponse> startSession(
            @Valid @RequestBody StartSessionRequest request,
            @CurrentUserId Long userId) {
        StartSessionDto dto = sessionMapper.toStartSessionDto(request, userId);

        StartSessionResponse sessionResponse = sessionMapper
                .startSessionDtoToStartSessionResponse(workSessionService.startSession(dto));

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponse);
    }

    @Operation(summary = "Finish an active work session", description = "Closes any dangling active intervals, calculates the final aggregate session duration, and persists increments to the assigned task/subtask.")
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<FinishSessionResponse> finishTimer(
            @PathVariable @Positive Long sessionId,
            @CurrentUserId Long userId) {
        FinishSessionResponse response = sessionMapper.finishSessionDtoToFinishSessionResponse(
                workSessionService.finishSession(sessionId, userId)
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Pause an active work session", description = "Suspends a RUNNING session, sets timestamps, and updates state to PAUSED.")
    @PostMapping("/{sessionId}/pause")
    public ResponseEntity<Void> pauseTimer(
            @PathVariable @Positive Long sessionId,
            @CurrentUserId Long userId) {
        workSessionService.pauseSession(sessionId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resume a paused work session", description = "Re-opens a new timeline interval tracking session slice for a PAUSED container.")
    @PostMapping("/{sessionId}/resume")
    public ResponseEntity<Void> resumeTimer(
            @PathVariable @Positive Long sessionId,
            @CurrentUserId Long userId) {
        workSessionService.resumeSession(sessionId, userId);
        return ResponseEntity.noContent().build();
    }
}