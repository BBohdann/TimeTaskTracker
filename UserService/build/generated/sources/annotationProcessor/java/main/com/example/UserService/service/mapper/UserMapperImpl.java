package com.example.UserService.service.mapper;

import com.example.UserService.controller.request.SignupRequest;
import com.example.UserService.controller.request.UpdatePasswordRequest;
import com.example.UserService.controller.responce.UserResponse;
import com.example.UserService.data.entity.User;
import com.example.UserService.service.dto.RegisterUserDto;
import com.example.UserService.service.dto.UpdatePasswordDto;
import com.example.UserService.service.dto.UserDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-18T06:18:11+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from Maven%20, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto userEntityToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setLogin( user.getLogin() );
        userDto.setEmail( user.getEmail() );
        userDto.setNickname( user.getNickname() );
        userDto.setLastUpdatedDate( user.getLastUpdatedDate() );
        userDto.setCreatedDate( user.getCreatedDate() );

        return userDto;
    }

    @Override
    public RegisterUserDto signupRequestToRegisterUserDto(SignupRequest request) {
        if ( request == null ) {
            return null;
        }

        RegisterUserDto registerUserDto = new RegisterUserDto();

        registerUserDto.setLogin( request.getLogin() );
        registerUserDto.setPassword( request.getPassword() );
        registerUserDto.setEmail( request.getEmail() );
        registerUserDto.setNickname( request.getNickname() );

        return registerUserDto;
    }

    @Override
    public UpdatePasswordDto updatePasswordRequestToDto(UpdatePasswordRequest request, Long id) {
        if ( request == null && id == null ) {
            return null;
        }

        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();

        if ( request != null ) {
            updatePasswordDto.setOldPassword( request.getOldPassword() );
            updatePasswordDto.setNewPassword( request.getNewPassword() );
        }
        updatePasswordDto.setId( id );

        return updatePasswordDto;
    }

    @Override
    public User registerUserDtoToUserEntity(RegisterUserDto dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setLogin( dto.getLogin() );
        user.setPassword( dto.getPassword() );
        user.setEmail( dto.getEmail() );
        user.setNickname( dto.getNickname() );

        return user;
    }

    @Override
    public UserResponse userDtoToUserResponse(UserDto dto) {
        if ( dto == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( dto.getId() );
        userResponse.setLogin( dto.getLogin() );
        userResponse.setEmail( dto.getEmail() );
        userResponse.setNickname( dto.getNickname() );
        userResponse.setCreatedDate( dto.getCreatedDate() );

        return userResponse;
    }
}
