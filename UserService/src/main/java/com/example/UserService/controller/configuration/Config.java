package com.example.UserService.controller.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Config {
    private final String jwtSecret;
    private final Long jwtExpiration;

    public Config(@Value("${JWT_SECRET}") String jwtSecret,
                  @Value("${JWT_EXPIRATION}") Long jwtExpiration
    ){
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }
}