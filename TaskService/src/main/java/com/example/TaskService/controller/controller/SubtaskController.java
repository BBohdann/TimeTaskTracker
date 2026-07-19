package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.configuration.mvc.CurrentUserId;
import com.example.TaskService.controller.request.subtask.CreateSubtaskRequest;
import com.example.TaskService.controller.request.subtask.SubtaskStatusRequest;
import com.example.TaskService.controller.request.subtask.UpdateSubtaskRequest;
import com.example.TaskService.controller.response.subtask.SubtaskCreatedResponse;
import com.example.TaskService.controller.response.subtask.SubtaskResponse;
import com.example.TaskService.controller.response.subtask.UpdatedSubtaskResponse;
import com.example.TaskService.service.dto.subtask.CreateSubtaskDto;
import com.example.TaskService.service.dto.subtask.SubtaskDto;
import com.example.TaskService.service.dto.subtask.UpdateSubtaskDto;
import com.example.TaskService.service.mapper.SubtaskMapper;
import com.example.TaskService.service.service.SubtaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/tasks/{taskId}/subtasks")
@RequiredArgsConstructor
@Tag(name = "Subtask Management", description = "Operations for nesting smaller subtasks under an existing main task boundary")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("isAuthenticated()")
public class SubtaskController {
    private final SubtaskMapper subtaskMapper;
    private final SubtaskService subtaskService;

    @Operation(summary = "Create a new subtask under a task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subtask nested successfully"),
            @ApiResponse(responseCode = "404", description = "Parent task not found")
    })
    @PostMapping
    public ResponseEntity<SubtaskCreatedResponse> createSubtask(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody CreateSubtaskRequest subtaskRequest,
            @CurrentUserId Long userId) {
        CreateSubtaskDto subtaskDto = subtaskMapper.subtaskRequestToCreateSubtaskDto(subtaskRequest);
        SubtaskDto saved = subtaskService.createSubtask(taskId, userId, subtaskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(subtaskMapper.subtaskDtoToCreatedResponse(saved));
    }

    @Operation(summary = "Update subtask information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subtask fields updated"),
            @ApiResponse(responseCode = "404", description = "Subtask or parent task not found")
    })
    @PatchMapping("/{subtaskId}")
    public ResponseEntity<UpdatedSubtaskResponse> updateSubtask(
            @PathVariable @Positive Long taskId,
            @PathVariable @Positive Long subtaskId,
            @Valid @RequestBody UpdateSubtaskRequest updateRequest,
            @CurrentUserId Long userId) {
        SubtaskDto updatedSubtask = subtaskService.updateSubtask(
                taskId, subtaskId, userId, subtaskMapper.updateSubtaskRequestToUpdateSubtaskDto(updateRequest)
        );
        return ResponseEntity.ok(subtaskMapper.subtaskDtoToUpdatedSubtaskResponse(updatedSubtask));
    }

    @Operation(summary = "Delete a subtask")
    @ApiResponse(responseCode = "204", description = "Subtask dropped")
    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(
            @PathVariable @Positive Long taskId,
            @PathVariable @Positive Long subtaskId,
            @CurrentUserId Long userId) {
        subtaskService.deleteSubtask(subtaskId, taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get subtask details by ID")
    @GetMapping("/{subtaskId}")
    public ResponseEntity<SubtaskResponse> getSubtask(
            @PathVariable @Positive Long taskId,
            @PathVariable @Positive Long subtaskId,
            @CurrentUserId Long userId) {
        SubtaskDto subtask = subtaskService.getSubtaskById(taskId, subtaskId, userId);
        return ResponseEntity.ok(subtaskMapper.subtaskDtoToResponse(subtask));
    }

    @Operation(summary = "Get all subtasks for a specific task filtered by status")
    @GetMapping
    public ResponseEntity<List<SubtaskResponse>> getSubtasks(
            @PathVariable @Positive Long taskId,
            @RequestParam SubtaskStatusRequest status,
            @CurrentUserId Long userId) {
        List<SubtaskDto> subtasks = subtaskService.getSubtasksByStatus(taskId, userId, status);
        return ResponseEntity.ok(subtaskMapper.subtaskDtoToSubtaskResponse(subtasks));
    }
}