package com.example.UserService.service.service;

import com.example.UserService.controller.configuration.jwt.UserDetailsImpl;
import com.example.UserService.data.entity.User;
import com.example.UserService.data.repository.UserRepository;
import com.example.UserService.service.dto.UserInfoDto;
import com.example.UserService.service.exception.*;
import com.example.UserService.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Transactional
    public void registerUser(String login, String password, String email, String nickname) {
        validateUniqueLoginAndEmail(login, email);

        User user = new User(login, encoder.encode(password), email, nickname);
        LocalDate now = LocalDate.now();
        user.setCreatedDate(now);
        user.setLastUpdatedDate(now);

        userRepository.save(user);
    }

    @Transactional
    public UserInfoDto updateLogin(String oldLogin, String newLogin) throws UserNotFoundException {
        User user = findUserByLogin(oldLogin);

        if (userRepository.existsByLogin(newLogin)) {
            throw new UserAlreadyExistException(newLogin);
        }

        user.setLogin(newLogin);
        user.setLastUpdatedDate(LocalDate.now());

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserInfoDto updateNickname(String login, String newNickname) throws UserNotFoundException {
        User user = findUserByLogin(login);

        user.setNickname(newNickname);
        user.setLastUpdatedDate(LocalDate.now());

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserInfoDto updatePassword(String login, String oldPassword, String newPassword) throws UserNotFoundException {
        User user = findUserByLogin(login);

        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new UserIncorrectPasswordException(login);
        }

        user.setPassword(encoder.encode(newPassword));
        user.setLastUpdatedDate(LocalDate.now());

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + login));

        return UserDetailsImpl.build(user);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getUserNickname(String login) {
        String nickname = userRepository.findNicknameByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Nickname for user with login %s not found", login)
                ));

        return Map.of("nickname", nickname);
    }

    private void validateUniqueLoginAndEmail(String login, String email) {
        if (userRepository.existsByLogin(login)) {
            throw new UserAlreadyExistException(login);
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException(email);
        }
    }

    private User findUserByLogin(String login) throws UserNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }
}
