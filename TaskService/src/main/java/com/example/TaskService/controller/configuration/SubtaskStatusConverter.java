package com.example.TaskService.controller.configuration;

import com.example.TaskService.controller.request.SubtaskStatusRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SubtaskStatusConverter
        implements Converter<String, SubtaskStatusRequest> {

    @Override
    public SubtaskStatusRequest convert(String source) {

        return SubtaskStatusRequest.valueOf(
                source.toUpperCase()
        );
    }
}