package com.example.UserService.service.service;

import com.example.UserService.controller.configuration.jwt.UserDetailsImpl;
import com.example.UserService.data.entity.User;
import com.example.UserService.data.repository.UserRepository;
import com.example.UserService.service.dto.UserInfoDto;
import com.example.UserService.service.exception.EmailAlreadyExistException;
import com.example.UserService.service.exception.UserAlreadyExistException;
import com.example.UserService.service.exception.UserIncorrectPasswordException;
import com.example.UserService.service.exception.UserNotFoundException;
import com.example.UserService.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Transactional
    public void registerUser(String login, String password,
                             String email, String nickname) throws UserAlreadyExistException, EmailAlreadyExistException {
        if (userRepository.existsByLogin(login))
            throw new UserAlreadyExistException(login);
        if (userRepository.existsByEmail(email))
            throw new EmailAlreadyExistException(email);
        User user = new User(login, encoder.encode(password), email, nickname);
        user.setCreatedDate(LocalDate.now());
        user.setLastUpdatedDate(LocalDate.now());
        userRepository.save(user);
    }

    @Transactional
    public UserInfoDto updateLogin(String oldLogin, String newLogin)
            throws UserNotFoundException, UserAlreadyExistException {
        User user = userRepository.findByLogin(oldLogin)
                .orElseThrow(() -> new UserNotFoundException(oldLogin));
        if (userRepository.existsByLogin(newLogin))
            throw new UserAlreadyExistException(newLogin);
        user.setLogin(newLogin);
        user.setLastUpdatedDate(LocalDate.now());

        return userMapper.toUserDto(userRepository.save(user));
    }

//    @Transactional
//    public UserInfoDto updateEmail(String login, String newEmail)
//            throws UserNotFoundException, EmailAlreadyExistException {
//        User user = userRepository.findByLogin(login)
//                .orElseThrow(() -> new UserNotFoundException(login));
//        if (userRepository.existsByEmail(newEmail))
//            throw new EmailAlreadyExistException(newEmail);
//        user.setEmail(newEmail);
//        return userMapper.toUserDto(userRepository.save(user));
//    }

    @Transactional
    public UserInfoDto updateNickname(String login, String newNickname)
            throws UserNotFoundException, EmailAlreadyExistException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
        user.setNickname(newNickname);
        user.setLastUpdatedDate(LocalDate.now());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserInfoDto updatePassword(String login, String oldPassword, String newPassword)
            throws UserNotFoundException, UserIncorrectPasswordException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
        if (encoder.matches(oldPassword, user.getPassword()) &&
                Objects.nonNull(login) && login.equals(user.getLogin())) {
            user.setPassword(encoder.encode(newPassword));
            user.setLastUpdatedDate(LocalDate.now());
            return userMapper.toUserDto(userRepository.save(user));
        } else
            throw new UserIncorrectPasswordException(login);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login).
                orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + login));
        return UserDetailsImpl.build(user);
    }
}
