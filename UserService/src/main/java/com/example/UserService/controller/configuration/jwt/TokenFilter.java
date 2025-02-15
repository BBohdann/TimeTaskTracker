package com.example.UserService.controller.configuration.jwt;

import com.example.UserService.service.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class TokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenUtils jwtUtils;

    @Autowired
    private UserService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = parseJtw(authHeader);
            if (token != null && jwtUtils.validateJwtToken(token)) {
                String login = jwtUtils.getLoginFromJwtToken(token);
                Long id = jwtUtils.getUserIdFromJwtToken(token);

                UserDetails userDetails = new UserDetailsImpl(id, login, null, token);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getAuthorities(), userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
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
//    private String parseJwt(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("json-token".equals(cookie.getName()))
//                    return cookie.getValue();
//            }
//        }
//        return null;
//    }
