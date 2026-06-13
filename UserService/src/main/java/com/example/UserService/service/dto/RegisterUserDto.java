package com.example.UserService.service.dto;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String login;
    private String password;
    private String email;
    private String nickname;
}
