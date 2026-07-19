package com.example.TaskService.controller.configuration;

import com.example.TaskService.controller.response.ErrorResponse;
import com.example.TaskService.service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class MainExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return buildError(HttpStatus.BAD_REQUEST, request.getRequestURI(), errors);
    }

    @ExceptionHandler({
            TaskNotFoundException.class,
            SubtaskNotFoundException.class,
            SessionNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                ex.getMessage()
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            SessionAlreadyExistsException.class,
            SessionLimitException.class,
            SessionStatusException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, request.getRequestURI(), "Access denied");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception on {}", request.getRequestURI(), ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                "Internal server error"
        );
    }

    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            String path,
            List<String> errors
    ) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status.value(), path, errors));
    }

    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            String path,
            String error
    ) {
        return buildError(status, path, List.of(error));
    }
}
