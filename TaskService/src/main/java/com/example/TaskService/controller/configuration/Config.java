package com.example.TaskService.controller.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Config {
    private final String jwtSecret;

    public Config(@Value("${JWT_SECRET}") String jwtSecret){
        this.jwtSecret = jwtSecret;
    }
}
