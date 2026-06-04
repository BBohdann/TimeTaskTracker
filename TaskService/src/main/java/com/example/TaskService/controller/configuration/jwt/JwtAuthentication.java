package com.example.TaskService.controller.configuration.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Collections;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private final String login;
    private final Long userId;

    public JwtAuthentication(String login, Long userId) {
        super(Collections.emptyList());
        this.login = login;
        this.userId = userId;
        super.setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return login;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Long getUserId() {
        return userId;
    }
}
