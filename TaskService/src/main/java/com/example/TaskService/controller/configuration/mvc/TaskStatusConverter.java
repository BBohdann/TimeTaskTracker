package com.example.TaskService.controller.configuration.mvc;

import com.example.TaskService.controller.request.TaskStatusRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusConverter implements Converter<String, TaskStatusRequest> {

    @Override
    public TaskStatusRequest convert(String source) {
        try {
            return TaskStatusRequest.valueOf(source.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status: " + source);
        }
    }
}