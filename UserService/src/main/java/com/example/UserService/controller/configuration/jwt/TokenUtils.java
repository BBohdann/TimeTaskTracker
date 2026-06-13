package com.example.UserService.controller.configuration.jwt;

import com.example.UserService.controller.configuration.Config;
import com.example.UserService.data.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenUtils {
    private final Config config;

    public String generateJwtToken(User user) {
        return generateToken(
                user.getId(),
                user.getLogin()
        );
    }

    public String generateJwtToken(Long userId, String login) {
        return generateToken(
                userId,
                login
        );
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String generateToken(Long userId, String login) {
        return Jwts.builder()
                .setId(userId.toString())
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + config.getJwtExpiration()
                        )
                )
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.getJwtSecret()));
    }
}
