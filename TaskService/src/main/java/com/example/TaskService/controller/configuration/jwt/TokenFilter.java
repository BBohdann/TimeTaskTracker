package com.example.TaskService.controller.configuration.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = parseJwt(request.getHeader("Authorization"));
        try {
            if (Objects.nonNull(token)) {
                Claims claims = tokenUtils.parseClaims(token);
                String login = claims.getSubject();

                Long userId = Long.parseLong(claims.getId());

                SecurityContextHolder.getContext()
                        .setAuthentication(
                                new JwtAuthentication(login, userId)
                        );
            }

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT authentication error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{" +
                    "errors: [Invalid or expired token]" +
                    "}");

            return;
        }
    }

    private String parseJwt(String header) {
        if (Objects.nonNull(header)
                && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
