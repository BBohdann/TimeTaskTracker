package com.example.UserService.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 100)
    private String login;

    @NotBlank
    @Size(min = 3, max = 100)
    private String password;

    @NotBlank
//    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 100)
    private String nickname;

}