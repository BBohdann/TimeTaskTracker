package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.configuration.mvc.CurrentUserId;
import com.example.TaskService.controller.request.*;
import com.example.TaskService.controller.response.SubtaskCreatedResponse;
import com.example.TaskService.controller.response.SubtaskResponse;
import com.example.TaskService.controller.response.UpdatedSubtaskResponse;
import com.example.TaskService.service.dto.CreateSubtaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import com.example.TaskService.service.mapper.SubtaskMapper;
import com.example.TaskService.service.service.SubtaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks/{taskId}/subtasks")
@RequiredArgsConstructor
@Tag(name = "Subtask Management", description = "API for managing subtasks")
@SecurityRequirement(name = "Bearer Authentication")
public class SubtaskController {
    private final SubtaskMapper subtaskMapper;
    private final SubtaskService subtaskService;

//    @Operation(summary = "Create a new subtask", description = "Allows the user to create a new subtask under an existing task. The Authorization header should contain the Bearer token.")
    @PostMapping
    public ResponseEntity<SubtaskCreatedResponse> createSubtask(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody CreateSubtaskRequest subtaskRequest,
            @CurrentUserId Long userId) {
        CreateSubtaskDto subtaskDto = subtaskMapper.subtaskRequestToCreateSubtaskDto(subtaskRequest);

        SubtaskDto saved = subtaskService.createSubtask(
                taskId,
                userId,
                subtaskDto
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subtaskMapper.subtaskDtoToCreatedResponse(saved));
    }

//    @Operation(summary = "Update time spent on a subtask", description = "Updates the time spent on a specific subtask. The Authorization header should contain the Bearer token.")
    @PatchMapping("/{subtaskId}/time-spent")
    public ResponseEntity<Void> updateSubtaskTimeSpent(
            @PathVariable @Positive Long taskId,
            @PathVariable @Positive Long subtaskId,
            @Valid @RequestBody UpdateTimeSpentRequest taskRequest,
            @CurrentUserId Long userId)  {

        subtaskService.updateSubtaskTimeSpent(taskId, subtaskId, userId, taskRequest.getTimeSpent());

        return ResponseEntity.noContent().build();
    }

//    @Operation(summary = "Update subtask", description = "Allows the user to update information for an existing subtask. The Authorization header should contain the Bearer token.")
    @PatchMapping("/{subtaskId}")
    public ResponseEntity<UpdatedSubtaskResponse> updateSubtask(
            @PathVariable @Positive Long taskId,
            @PathVariable @Positive Long subtaskId,
            @Valid @RequestBody UpdateSubtaskRequest updateRequest,
            @CurrentUserId Long userId) {
        SubtaskDto updatedSubtask = subtaskService.updateSubtask(
                taskId,
                subtaskId,
                userId,
                subtaskMapper.updateSubtaskRequestToSubtaskDto(updateRequest)
        );

        return ResponseEntity.ok(
                subtaskMapper.subtaskDtoToUpdatedSubtaskResponse(updatedSubtask));
    }


//    @Operation(summary = "Delete a subtask", description = "Deletes a subtask by its ID. The Authorization header should contain the Bearer token.")
    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(
            @PathVariable @Positive Long taskId,
            @PathVariable @Positive Long subtaskId,
            @CurrentUserId Long userId) {
        subtaskService.deleteSubtask(subtaskId, taskId, userId);

        return ResponseEntity.noContent().build();
    }

//    @Operation(summary = "Get subtask by ID", description = "Fetches subtask information by its ID. The Authorization header should contain the Bearer token.")
    @GetMapping("/{subtaskId}")
    public ResponseEntity<SubtaskResponse> getSubtask(
            @PathVariable @Positive Long taskId,
            @PathVariable @Positive Long subtaskId,
            @CurrentUserId Long userId) {
        SubtaskDto subtask = subtaskService.getSubtaskById(
                taskId,
                subtaskId,
                userId
        );

        return ResponseEntity.ok(subtaskMapper.subtaskDtoToResponse(subtask));
    }

    @GetMapping
    public ResponseEntity<List<SubtaskResponse>> getSubtasks(
            @PathVariable @Positive Long taskId,
            @RequestParam SubtaskStatusRequest status,
            @CurrentUserId Long userId) {
        List<SubtaskDto> subtasks = subtaskService.getSubtasksByStatus(
                taskId,
                userId,
                status
        );

        return ResponseEntity.ok(
                subtaskMapper.subtaskDtosToSubtaskResponses(subtasks)
        );
    }
}