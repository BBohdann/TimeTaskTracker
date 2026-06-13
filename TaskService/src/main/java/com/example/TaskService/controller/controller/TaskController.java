package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.configuration.mvc.CurrentUserId;
import com.example.TaskService.controller.request.CreateTaskRequest;
import com.example.TaskService.controller.request.TaskStatusRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.controller.request.UpdateTimeSpentRequest;
import com.example.TaskService.controller.response.TaskCreatedResponse;
import com.example.TaskService.controller.response.TaskResponse;
import com.example.TaskService.controller.response.TaskUpdatedResponse;
import com.example.TaskService.service.dto.TaskDto;
import com.example.TaskService.service.dto.TaskWithSubtasksDto;
import com.example.TaskService.service.mapper.TaskMapper;
import com.example.TaskService.service.service.TaskService;
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
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "API for managing tasks")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    //    @Operation(summary = "Create a new task", description = "Allows the user to create a new task. The Authorization header should contain the Bearer token.")
    @PostMapping
    public ResponseEntity<TaskCreatedResponse> createTask(
            @Valid @RequestBody CreateTaskRequest taskRequest,
            @CurrentUserId Long userId) {
        TaskDto createdTask = taskService.createTask(
                taskMapper.taskRequestToCreateTaskDto(
                        taskRequest,
                        userId)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskMapper.taskDtoToTaskCreatedResponse(createdTask));
    }

    //    @Operation(summary = "Update time spent on a task", description = "Updates the time spent on a specific task. The Authorization header should contain the Bearer token.")
    @PatchMapping("/{taskId}/time-spent")
    public ResponseEntity<Void> updateTimeSpent(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody UpdateTimeSpentRequest request,
            @CurrentUserId Long userId) {
        taskService.updateTimeSpent(
                taskId,
                request.getTimeSpent(),
                userId
        );

        return ResponseEntity.noContent().build();
    }

    //    @Operation(summary = "Update task", description = "Allows the user to update task information. The Authorization header should contain the Bearer token.")
    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskUpdatedResponse> updateTask(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @CurrentUserId Long userId) {
        TaskDto updatedTask = taskService.updateTask(
                taskId,
                taskMapper.updateTaskRequestToUpdateTaskDto(request),
                userId
        );

        return ResponseEntity.ok(taskMapper.taskDtoToTaskUpdatedResponse(updatedTask));
    }

    //    @Operation(summary = "Delete a task", description = "Deletes a task by its ID. The Authorization header should contain the Bearer token.")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable @Positive Long taskId,
            @CurrentUserId Long userId) {
        taskService.deleteTask(taskId, userId);

        return ResponseEntity.noContent().build();
    }

    //    @Operation(summary = "Get task by ID", description = "Fetches task information by its ID. The Authorization header should contain the Bearer token.")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable @Positive Long taskId,
            @CurrentUserId Long userId) {
        TaskWithSubtasksDto task = taskService.getTaskById(taskId, userId);

        return ResponseEntity.ok(
                taskMapper.taskWithSubtasksDtoToTaskResponse(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam TaskStatusRequest status,
            @CurrentUserId Long userId) {
        List<TaskWithSubtasksDto> tasks = taskService.getTasksByStatus(
                userId,
                status
        );

        return ResponseEntity.ok(
                taskMapper.taskWithSubtasksDtosToTaskResponses(tasks));
    }
}
