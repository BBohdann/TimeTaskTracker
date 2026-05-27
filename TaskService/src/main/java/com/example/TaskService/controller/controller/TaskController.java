package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.configuration.jwt.JwtAuthentication;
import com.example.TaskService.controller.request.CreateTaskRequest;
import com.example.TaskService.controller.request.TaskStatusRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.controller.request.UpdateTimeSpentRequest;
import com.example.TaskService.controller.response.TaskBaseResponse;
import com.example.TaskService.service.dto.TaskDto;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.TaskMapper;
import com.example.TaskService.service.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskBaseResponse> addTask(@Valid @RequestBody CreateTaskRequest taskRequest) {
        TaskDto taskDto = taskService.addTask(
                taskMapper.taskRequestToDto(
                        taskRequest, getUserIdFromAuth())
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskMapper.toTaskBaseResponse(taskDto));
    }

    //    @Operation(summary = "Update time spent on a task", description = "Updates the time spent on a specific task. The Authorization header should contain the Bearer token.")
    @PatchMapping("/{taskId}/time-spent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> updateSpentTime(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody UpdateTimeSpentRequest request) {
        TaskDto updatedTask = taskService.updateTimeSpent(
                taskId,
                request.getTimeSpent(),
                getUserIdFromAuth()
        );

        return ResponseEntity.ok(updatedTask);
    }

    //    @Operation(summary = "Update task", description = "Allows the user to update task information. The Authorization header should contain the Bearer token.")
    @PatchMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskDto updatedTask = taskService.updateTask(
                taskId,
                request,
                getUserIdFromAuth()
        );

        return ResponseEntity.ok(updatedTask);
    }

    //    @Operation(summary = "Delete a task", description = "Deletes a task by its ID. The Authorization header should contain the Bearer token.")
    @DeleteMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public void deleteTaskById(@PathVariable @Positive Long taskId) {
        taskService.deleteTask(taskId, getUserIdFromAuth());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDto>> getTasks(@RequestParam TaskStatusRequest status) {

        List<TaskDto> tasks = taskService.getTasksByStatus(
                getUserIdFromAuth(),
                status
        );

        return ResponseEntity.ok(tasks);
    }

    //    @Operation(summary = "Get task by ID", description = "Fetches task information by its ID. The Authorization header should contain the Bearer token.")
    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> getTask(@PathVariable @Positive Long taskId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.getTaskById(taskId));
    }

    private Long getUserIdFromAuth() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}
