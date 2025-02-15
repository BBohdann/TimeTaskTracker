package com.example.UserService.controller.configuration;

import com.example.UserService.service.exception.EmailAlreadyExistException;
import com.example.UserService.service.exception.UserAlreadyExistException;
import com.example.UserService.service.exception.UserIncorrectPasswordException;
import com.example.UserService.service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
            UserAlreadyExistException.class,
            UserIncorrectPasswordException.class,
            EmailAlreadyExistException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<Map<String, List<String>>> conflictException(Exception ex) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("errors", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Map<String, List<String>>> anotherException(Exception ex) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("errors", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}