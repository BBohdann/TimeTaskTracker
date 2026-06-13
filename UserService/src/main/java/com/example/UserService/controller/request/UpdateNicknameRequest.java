package com.example.UserService.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateNicknameRequest {
    @Length(max = 100)
    @NotBlank
    private String newNickname;
}