package com.example.TaskService.controller.response;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String path;
    private List<String> errors;

    public ErrorResponse(int status, String path, List<String> errors) {
        this.timestamp = Instant.now();
        this.status = status;
        this.path = path;
        this.errors = errors;
    }

    public ErrorResponse(int status, String path, String error) {
        this(status, path, List.of(error));
    }
}