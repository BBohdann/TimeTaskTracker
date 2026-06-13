package com.example.UserService.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DeleteUserRequest {
    @NotBlank
    @Length(max = 100)
    private String password;
}
