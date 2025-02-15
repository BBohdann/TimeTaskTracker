package com.example.TaskService.controller.configuration.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class TokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = parseJtw(request.getHeader("Authorization"));
            if (Objects.nonNull(token) && jwtUtils.validateJwtToken(token)) {
                String login = jwtUtils.getLoginFromJwtToken(token);
                Long userId = jwtUtils.getUserIdFromJwtToken(token);

                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(login, userId));
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJtw(String header) {
        if (Objects.nonNull(header) && header.startsWith("Bearer "))
            return header.substring(7);
        return null;
    }
}
