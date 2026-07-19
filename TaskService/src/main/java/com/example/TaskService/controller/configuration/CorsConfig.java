package com.example.TaskService.controller.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] allowedIps = generateAllowedIps(101, 110);
                String[] allowedOriginPatterns = mergeAllowedOriginPatterns(allowedIps);

                registry.addMapping("/**")
                        .allowedOriginPatterns(allowedOriginPatterns)
                        .allowedMethods("GET", "POST", "PATCH", "DELETE")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Content-Security-Policy")
                        .allowCredentials(true);
            }

            private String[] generateAllowedIps(int start, int end) {
                return IntStream.rangeClosed(start, end)
                        .mapToObj(i -> "http://192.168.0." + i + ":3000")
                        .toArray(String[]::new);
            }

            private String[] mergeAllowedOriginPatterns(String[] dynamicIps) {
                List<String> origins = new ArrayList<>(Arrays.asList(dynamicIps));
                origins.add("https://*.netlify.app");
                origins.add("https://*.github.io");
                return origins.toArray(new String[0]);
            }
        };
    }
}