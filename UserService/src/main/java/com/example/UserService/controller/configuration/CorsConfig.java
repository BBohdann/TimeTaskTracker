package com.example.UserService.controller.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {
    private final Config config;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] allowedIps = generateAllowedIps(101, 110);
                String[] allowedOrigins = mergeAllowedOrigins(allowedIps);

                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true);
            }

            private String[] generateAllowedIps(int start, int end) {
                return IntStream.rangeClosed(start, end)
                        .mapToObj(i -> "http://192.168.0." + i + ":3000")
                        .toArray(String[]::new);
            }

            private String[] mergeAllowedOrigins(String[] dynamicIps) {
                List<String> origins = new ArrayList<>();
                for (String ip : dynamicIps) {
                    origins.add(ip);
                }

                origins.add("https://*.netlify.app");
                origins.add("https://*.github.io");

                return origins.toArray(new String[0]);
            }
        };
    }
}
