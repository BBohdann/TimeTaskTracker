package com.example.UserService.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLoginRequest {

    @NotBlank
    @Size(min = 3, max = 100)
    private String newLogin;
}
