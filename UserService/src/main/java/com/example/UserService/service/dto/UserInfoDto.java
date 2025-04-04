package com.example.UserService.service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfoDto {
    private Long id;
    private String login;
    private String email;
    private String nickname;
    private LocalDate lastUpdatedDate;
    private LocalDate createdDate;
}
