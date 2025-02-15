package com.example.UserService.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdatePasswordRequest {

    @NotBlank
    @Length(max = 100)
    private String oldPassword;

    @NotBlank
    @Length(max = 100)
    private String newPassword;
}
