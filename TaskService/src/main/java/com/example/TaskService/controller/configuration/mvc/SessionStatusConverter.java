package com.example.TaskService.controller.configuration.mvc;

import com.example.TaskService.controller.request.session.SessionStatusRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SessionStatusConverter implements Converter<String, SessionStatusRequest> {
    @Override
    public SessionStatusRequest convert(String source) {
        try {
            return SessionStatusRequest.valueOf(source.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status: " + source);
        }
    }
}
