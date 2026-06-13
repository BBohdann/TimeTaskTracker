package com.example.UserService.service.service;

import com.example.UserService.data.entity.User;
import com.example.UserService.data.repository.UserRepository;
import com.example.UserService.service.dto.RegisterUserDto;
import com.example.UserService.service.dto.UpdatePasswordDto;
import com.example.UserService.service.dto.UserDto;
import com.example.UserService.service.dto.UserTokenData;
import com.example.UserService.service.exception.*;
import com.example.UserService.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Transactional
    public void registerUser(RegisterUserDto dto) {
        validateUniqueUser(dto.getLogin(), dto.getEmail());

        User user = userMapper.registerUserDtoToUserEntity(dto);
        user.setPassword(encoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserTokenData updateLogin(Long id, String newLogin) {
        User user = getUserOrThrow(id);
        if (userRepository.existsByLogin(newLogin)) {
            throw new UserAlreadyExistException(newLogin);
        }

        user.setLogin(newLogin);

        return new UserTokenData(
                user.getId(),
                user.getLogin()
        );
    }

    @Transactional
    public void updateEmail(Long id, String newEmail){
        User user = getUserOrThrow(id);

        if (userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistException(newEmail);
        }

        user.setEmail(newEmail);
    }

    @Transactional
    public void deleteUser(Long id, String password){
        User user = getUserOrThrow(id);

        if (!encoder.matches(password, user.getPassword())) {
            throw new UserIncorrectPasswordException(user.getLogin());
        }

        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id){
        return userRepository.findById(id)
                .map(userMapper::userEntityToUserDto)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void updateNickname(Long id, String newNickname) {
        User user = getUserOrThrow(id);

        user.setNickname(newNickname);
    }

    @Transactional
    public void updatePassword(UpdatePasswordDto dto) {
        User user = getUserOrThrow(dto.getId());

        if (!encoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new UserIncorrectPasswordException(dto.getId().toString());
        }

        user.setPassword(
                encoder.encode(
                        dto.getNewPassword())
        );
    }

    private void validateUniqueUser(String login, String email) {
        if (userRepository.existsByLogin(login)) {
            throw new UserAlreadyExistException(login);
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException(email);
        }
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
