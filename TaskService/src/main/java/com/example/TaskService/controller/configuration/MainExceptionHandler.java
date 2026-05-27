package com.example.TaskService.controller.configuration;

import com.example.TaskService.service.exception.SubtaskNotFoundException;
import com.example.TaskService.service.exception.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {

            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            errors.computeIfAbsent("errors", key -> new ArrayList<>())
                    .add(fieldName + ": " + errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({TaskNotFoundException.class,
                       SubtaskNotFoundException.class})
    public ResponseEntity<Map<String, List<String>>> handleNotFound(RuntimeException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        errors.put(
                "errors",
                Collections.singletonList(ex.getMessage())
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, List<String>>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {

        Map<String, List<String>> errors = new HashMap<>();

        errors.put(
                "errors",
                Collections.singletonList(ex.getMessage())
        );

        return ResponseEntity
                .badRequest()
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<String>>> handleGenericException(Exception ex) {
        Map<String, List<String>> errors = new HashMap<>();
        errors.put(
                "errors",
                Collections.singletonList("Internal server error")
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errors);
    }
}
