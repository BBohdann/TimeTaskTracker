package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.configuration.jwt.JwtAuthentication;
import com.example.TaskService.controller.request.CreateTaskRequest;
import com.example.TaskService.controller.request.UpdateTaskRequest;
import com.example.TaskService.controller.request.UpdateTimeSpentRequest;
import com.example.TaskService.controller.responce.TaskBaseResponse;
import com.example.TaskService.service.dto.TaskDto;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.TaskMapper;
import com.example.TaskService.service.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("api/task")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "API for managing tasks")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Allows the user to create a new task. The Authorization header should contain the Bearer token.")
    @PostMapping("/createTask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskBaseResponse> addTask(@Valid @RequestBody CreateTaskRequest taskRequest,
                                                    HttpServletRequest request) {
        TaskDto taskDto = taskService.addTask(taskMapper.taskRequestToDto(taskRequest, getUserIdFromAuth()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(taskMapper.toTaskBaseResponse(taskDto));
    }

    @Operation(summary = "Update time spent on a task", description = "Updates the time spent on a specific task. The Authorization header should contain the Bearer token.")
    @PostMapping("/updateSpentTime")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> updateSpentTime(@Valid @RequestBody UpdateTimeSpentRequest taskRequest,
                                                   HttpServletRequest request) throws TaskNotFoundException {
        TaskDto dto = new TaskDto();
        dto.setId(taskRequest.getId());
        dto.setTimeSpent(taskRequest.getTimeSpent());
        dto.setUserId(getUserIdFromAuth());
        TaskDto updatedTask = taskService.updateTimeSpent(dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(updatedTask);
    }

    @Operation(summary = "Update task", description = "Allows the user to update task information. The Authorization header should contain the Bearer token.")
    @PostMapping("/updateTask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> updateTask(@Valid @RequestBody UpdateTaskRequest updateTaskRequest,
                           HttpServletRequest request) throws TaskNotFoundException {
        TaskDto updatedTask = taskService.updateTask(taskMapper.updateTaskRequestToTaskDto(updateTaskRequest, getUserIdFromAuth()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(updatedTask);
    }

    @Operation(summary = "Delete a task", description = "Deletes a task by its ID. The Authorization header should contain the Bearer token.")
    @PostMapping("/deleteTask")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTaskById(@RequestParam String id,
                               HttpServletRequest request,
                               HttpServletResponse response) throws TaskNotFoundException {
        taskService.deleteTask(Long.parseLong(id), getUserIdFromAuth());
        response.addHeader("Authorization", "Bearer " + getToken(request));
    }

    @Operation(summary = "Get all tasks", description = "Fetches all tasks for the authenticated user. The Authorization header should contain the Bearer token.")
    @GetMapping("/findAllTasks")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskDto>> getUserTasks(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(taskMapper.toTaskDtos(taskService.getTasksByUserId(getUserIdFromAuth())));
    }

    @Operation(summary = "Get active tasks", description = "Fetches all active tasks for the authenticated user. The Authorization header should contain the Bearer token.")
    @GetMapping("/findActiveTasks")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskDto>> getUserActiveTasks(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(taskMapper.toTaskDtos(taskService.getActiveTasksByUserId(getUserIdFromAuth())));
    }

    @Operation(summary = "Get task by ID", description = "Fetches task information by its ID. The Authorization header should contain the Bearer token.")
    @GetMapping("/getTask")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskDto> getTask(@RequestParam @NotNull String id,
                                           HttpServletRequest request) throws TaskNotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(taskService.getTaskById(Long.parseLong(id)));
    }

    private static String getToken(HttpServletRequest request) {
        Optional<String> token = request.getHeader("Authorization")
                .substring(7)
                .describeConstable();
        if (token.isEmpty())
            throw new InvalidCookieException("No token in header");
        return token.get();
    }

    private Long getUserIdFromAuth() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}
