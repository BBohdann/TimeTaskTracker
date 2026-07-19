package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.configuration.mvc.CurrentUserId;
import com.example.TaskService.controller.request.task.CreateTaskRequest;
import com.example.TaskService.controller.request.task.TaskStatusRequest;
import com.example.TaskService.controller.request.task.UpdateTaskRequest;
import com.example.TaskService.controller.response.task.TaskCreatedResponse;
import com.example.TaskService.controller.response.task.TaskResponse;
import com.example.TaskService.controller.response.task.TaskUpdatedResponse;
import com.example.TaskService.service.dto.task.TaskDto;
import com.example.TaskService.service.dto.task.TaskWithSubtasksDto;
import com.example.TaskService.service.mapper.TaskMapper;
import com.example.TaskService.service.service.TaskService;
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
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "Operations for creating, modifying, deleting, and fetching main tasks")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("isAuthenticated()")
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Creates a main task entry associated with the authorized user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload provided")
    })
    @PostMapping
    public ResponseEntity<TaskCreatedResponse> createTask(
            @Valid @RequestBody CreateTaskRequest taskRequest,
            @CurrentUserId Long userId) {
        TaskDto createdTask = taskService.createTask(taskMapper.taskRequestToCreateTaskDto(taskRequest, userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.taskDtoToTaskCreatedResponse(createdTask));
    }

    @Operation(summary = "Update task information", description = "Allows patching text fields, deadlines, or changing the execution status of a task.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully updated"),
            @ApiResponse(responseCode = "404", description = "Task not found or does not belong to the user")
    })
    @Valid
    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskUpdatedResponse> updateTask(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @CurrentUserId Long userId) {
        TaskDto updatedTask = taskService.updateTask(taskId, taskMapper.updateTaskRequestToUpdateTaskDto(request), userId);
        return ResponseEntity.ok(taskMapper.taskDtoToTaskUpdatedResponse(updatedTask));
    }

    @Operation(summary = "Delete a task by ID", description = "Permanently removes a task and all its associated subtasks.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable @Positive Long taskId,
            @CurrentUserId Long userId) {
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get task details by ID", description = "Fetches comprehensive info about a specific task, including an array of its subtasks.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task details"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable @Positive Long taskId,
            @CurrentUserId Long userId) {
        TaskWithSubtasksDto task = taskService.getTaskById(taskId, userId);
        return ResponseEntity.ok(taskMapper.taskWithSubtasksDtoToTaskResponse(task));
    }

    @Operation(summary = "Get tasks filtered by status", description = "Retrieves lists of active, inactive, or all tasks mapped to the authorized user.")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam TaskStatusRequest status,
            @CurrentUserId Long userId) {
        List<TaskWithSubtasksDto> tasks = taskService.getTasksByStatus(userId, status);
        return ResponseEntity.ok(taskMapper.taskWithSubtaskDtoToTaskResponse(tasks));
    }
}