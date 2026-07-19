package com.example.UserService.service.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserDto {
    private Long id;
    private String login;
    private String email;
    private String nickname;
    private Instant lastUpdatedDate;
    private Instant createdDate;
}
