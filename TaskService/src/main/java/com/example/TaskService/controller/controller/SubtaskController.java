package com.example.TaskService.controller.controller;

import com.example.TaskService.controller.request.CreateSubtaskRequest;
import com.example.TaskService.controller.request.UpdateSubtaskRequest;
import com.example.TaskService.controller.request.UpdateTimeSpentRequest;
import com.example.TaskService.controller.responce.SubtaskResponce;
import com.example.TaskService.service.dto.CreateSubtaskDto;
import com.example.TaskService.service.dto.SubtaskDto;
import com.example.TaskService.service.exception.SubtaskNotFoundException;
import com.example.TaskService.service.exception.TaskNotFoundException;
import com.example.TaskService.service.mapper.SubtaskMapper;
import com.example.TaskService.service.service.SubtaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/subtask")
@RequiredArgsConstructor
@Tag(name = "Subtask Management", description = "API for managing subtasks")
@SecurityRequirement(name = "Bearer Authentication")
public class SubtaskController {
    private final SubtaskMapper subtaskMapper;
    private final SubtaskService subtaskService;

    @Operation(summary = "Create a new subtask", description = "Allows the user to create a new subtask under an existing task. The Authorization header should contain the Bearer token.")
    @PostMapping("/createSubtask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubtaskResponce> addTask(@Valid @RequestBody CreateSubtaskRequest subtaskRequest,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws TaskNotFoundException {
        CreateSubtaskDto subtaskDto = subtaskMapper.subtaskRequestToDto(subtaskRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(subtaskMapper.subtaskDtoToResponce(subtaskService.addSubtask(subtaskDto)));
    }

    @Operation(summary = "Update time spent on a subtask", description = "Updates the time spent on a specific subtask. The Authorization header should contain the Bearer token.")
    @PostMapping("/updateSubtaskSpentTime")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateSubtaskSpentTime(@Valid @RequestBody UpdateTimeSpentRequest taskRequest,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) throws TaskNotFoundException, SubtaskNotFoundException {
        SubtaskDto dto = new SubtaskDto();
        dto.setId(taskRequest.getId());
        dto.setTimeSpent(taskRequest.getTimeSpent());
        subtaskService.updateSubtaskTimeSpent(dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .build();
    }

    @Operation(summary = "Update subtask", description = "Allows the user to update information for an existing subtask. The Authorization header should contain the Bearer token.")
    @PostMapping("/updateSubtask")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public void updateTask(@Valid @RequestBody UpdateSubtaskRequest updateSubtaskRequest,
                           HttpServletRequest request,
                           HttpServletResponse response) throws TaskNotFoundException, SubtaskNotFoundException {
        subtaskService.updateSubtask(subtaskMapper.updateSubtaskRequestToSubtaskDto(updateSubtaskRequest));
        response.addHeader("Authorization", "Bearer " + getToken(request));
    }

    @Operation(summary = "Delete a subtask", description = "Deletes a subtask by its ID. The Authorization header should contain the Bearer token.")
    @PostMapping("/deleteSubtask")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTaskById(@RequestParam String id,
                               HttpServletRequest request,
                               HttpServletResponse response) throws SubtaskNotFoundException {
        subtaskService.deleteSubtask(Long.parseLong(id));
        response.addHeader("Authorization", "Bearer " + getToken(request));
    }

    @Operation(summary = "Get subtask by ID", description = "Fetches subtask information by its ID. The Authorization header should contain the Bearer token.")
    @GetMapping("/getSubtask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubtaskDto> getSubtaskById(@RequestParam String id,
                                                     HttpServletRequest request) throws SubtaskNotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", "Bearer " + getToken(request))
                .body(subtaskService.getSubtaskById(Long.parseLong(id)));
    }

    private static String getToken(HttpServletRequest request) {
        Optional<String> token = request.getHeader("Authorization")
                .substring(7)
                .describeConstable();
        if (token.isEmpty())
            throw new InvalidCookieException("No token in header");
        return token.get();
    }
}