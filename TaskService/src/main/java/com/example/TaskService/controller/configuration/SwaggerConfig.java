package com.example.TaskService.controller.configuration;

import com.example.TaskService.controller.configuration.mvc.CurrentUserId;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    static {
        org.springdoc.core.utils.SpringDocUtils.getConfig()
                .addAnnotationsToIgnore(CurrentUserId.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task & Track Microservice API")
                        .version("1.0.0")
                        .description("Manages tracking, core timelines, tasks, and task splits."))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)));
    }

    @Bean
    public GroupedOpenApi taskApi() {
        return GroupedOpenApi.builder()
                .group("task-service: tasks")
                .pathsToMatch("/api/tasks/**")
                .pathsToExclude("/api/tasks/*/subtasks/**")
                .build();
    }

    @Bean
    public GroupedOpenApi subtaskApi() {
        return GroupedOpenApi.builder()
                .group("task-service: subtasks")
                .pathsToMatch("/api/tasks/*/subtasks/**")
                .build();
    }

    @Bean
    public GroupedOpenApi sessionApi() {
        return GroupedOpenApi.builder()
                .group("task-service: sessions")
                .pathsToMatch("/api/sessions/**")
                .build();
    }
}