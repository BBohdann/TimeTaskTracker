package com.example.UserService.service.mapper;

import com.example.UserService.controller.request.SignupRequest;
import com.example.UserService.controller.request.UpdatePasswordRequest;
import com.example.UserService.controller.responce.UserResponse;
import com.example.UserService.data.entity.User;
import com.example.UserService.service.dto.RegisterUserDto;
import com.example.UserService.service.dto.UpdatePasswordDto;
import com.example.UserService.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userEntityToUserDto(User user);

    RegisterUserDto signupRequestToRegisterUserDto(SignupRequest request);

    @Mapping(source = "id", target = "id")
    UpdatePasswordDto updatePasswordRequestToDto(UpdatePasswordRequest request, Long id);

    User registerUserDtoToUserEntity(RegisterUserDto dto);

    UserResponse userDtoToUserResponse(UserDto dto);
}
