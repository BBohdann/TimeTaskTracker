package com.example.UserService.controller.responce;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserResponse {
    private Long id;
    private String login;
    private String email;
    private String nickname;
    private Instant createdDate;
}
