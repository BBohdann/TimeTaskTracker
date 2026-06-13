package com.example.UserService.service.mapper;

import com.example.UserService.controller.request.SignupRequest;
import com.example.UserService.controller.request.UpdatePasswordRequest;
import com.example.UserService.controller.responce.UserResponse;
import com.example.UserService.data.entity.User;
import com.example.UserService.service.dto.RegisterUserDto;
import com.example.UserService.service.dto.UpdatePasswordDto;
import com.example.UserService.service.dto.UserDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {
    public UserDto userEntityToUserDto(User user){
        UserDto dto = new UserDto();
        dto.setLogin(user.getLogin());
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setLastUpdatedDate(user.getLastUpdatedDate());
        dto.setCreatedDate(user.getCreatedDate());
        return dto;
    }

    public RegisterUserDto signupRequestToRegisterUserDto(SignupRequest request){
        RegisterUserDto dto = new RegisterUserDto();

        dto.setEmail(request.getEmail());
        dto.setLogin(request.getLogin());
        dto.setNickname(request.getNickname());
        dto.setPassword(request.getPassword());

        return dto;
    }

    public UpdatePasswordDto updatePasswordRequestToDto (UpdatePasswordRequest request, Long id){
        UpdatePasswordDto dto = new UpdatePasswordDto();

        dto.setId(id);
        dto.setNewPassword(request.getNewPassword());
        dto.setOldPassword(request.getOldPassword());

        return dto;
    }


    public User registerUserDtoToUserEntity(RegisterUserDto dto){
        User entity = new User();

        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setNickname(dto.getNickname());

        return entity;
    }

    public UserResponse userDtoToUserResponse(UserDto dto){
        UserResponse response = new UserResponse();

        response.setId(dto.getId());
        response.setEmail(dto.getEmail());
        response.setLogin(dto.getLogin());
        response.setNickname(dto.getNickname());
        response.setCreatedDate(dto.getCreatedDate());

        return response;
    }
}
