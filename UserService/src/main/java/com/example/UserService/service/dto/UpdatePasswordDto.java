package com.example.UserService.service.dto;

import lombok.Data;

@Data
public class UpdatePasswordDto {
    private Long id;
    private String oldPassword;
    private String newPassword;
}
