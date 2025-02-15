package com.example.UserService.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {

    private String oldLogin;
    private String oldPassword;
    private String oldEmail;
    private String newLogin;
    private String newPassword;
    private String newEmail;
}