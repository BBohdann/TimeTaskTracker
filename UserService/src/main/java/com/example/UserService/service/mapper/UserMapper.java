package com.example.UserService.service.mapper;

import com.example.UserService.data.entity.User;
import com.example.UserService.service.dto.UserInfoDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserInfoDto toUserDto(User user){
        UserInfoDto dto = new UserInfoDto();
        dto.setLogin(user.getLogin());
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setLastUpdatedDate(user.getLastUpdatedDate());
        dto.setCreatedDate(user.getCreatedDate());
        return dto;
    }
}
