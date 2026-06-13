package com.example.UserService.controller.configuration.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private final Long userId;
    private final String login;

    public JwtAuthentication(Long userId, String login) {
        super(Collections.emptyList());
        this.userId = userId;
        this.login = login;

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

    public String getLogin() {
        return login;
    }
}