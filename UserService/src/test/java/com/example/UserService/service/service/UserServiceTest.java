package com.example.UserService.service.service;

import com.example.UserService.data.entity.User;
import com.example.UserService.data.repository.UserRepository;
import com.example.UserService.service.dto.UserInfoDto;
import com.example.UserService.service.exception.EmailAlreadyExistException;
import com.example.UserService.service.exception.UserAlreadyExistException;
import com.example.UserService.service.exception.UserIncorrectPasswordException;
import com.example.UserService.service.exception.UserNotFoundException;
import com.example.UserService.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userMapper = new UserMapper();
        userService = new UserService(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void registerUser_Success() throws UserAlreadyExistException, EmailAlreadyExistException {
        String login = "testLogin";
        String password = "testPassword";
        String email = "testEmail";
        String nickname = "testNickname";

        when(userRepository.existsByLogin(login)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        userService.registerUser(login, password, email, nickname);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(login, savedUser.getLogin());
        assertEquals(email, savedUser.getEmail());
        assertEquals(nickname, savedUser.getNickname());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(LocalDate.now(), savedUser.getCreatedDate());
        assertEquals(LocalDate.now(), savedUser.getLastUpdatedDate());
    }

    @Test
    void updateLogin_Success() throws UserNotFoundException, UserAlreadyExistException {
        String oldLogin = "oldUser";
        String newLogin = "newUser";

        User user = new User(oldLogin, "encodedPassword", "email@test.com", "nickname");
        user.setLastUpdatedDate(LocalDate.now().minusDays(1));

        when(userRepository.findByLogin(oldLogin)).thenReturn(Optional.of(user));
        when(userRepository.existsByLogin(newLogin)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInfoDto result = userService.updateLogin(oldLogin, newLogin);

        assertEquals(newLogin, result.getLogin());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getNickname(), result.getNickname());
        assertEquals(LocalDate.now(), user.getLastUpdatedDate());

        verify(userRepository).findByLogin(oldLogin);
        verify(userRepository).existsByLogin(newLogin);
        verify(userRepository).save(user);
    }

    @Test
    void updateNickname_Success() throws UserNotFoundException {
        String login = "testLogin";
        String newNickname = "testNickname";

        User user = new User(login, "encodedPassword", "email@test.com", "oldNickname");
        user.setId(1L);
        user.setLastUpdatedDate(LocalDate.now().minusDays(1));

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInfoDto result = userService.updateNickname(login, newNickname);

        assertEquals(newNickname, result.getNickname());
        assertEquals(LocalDate.now(), user.getLastUpdatedDate());

        verify(userRepository).findByLogin(login);
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_Success() throws UserNotFoundException, UserIncorrectPasswordException {
        String login = "testUser";
        String oldPassword = "oldPass";
        String newPassword = "newPass";
        String encodedOldPassword = "encodedOldPass";
        String encodedNewPassword = "encodedNewPass";

        User user = new User(login, encodedOldPassword, "email@test.com", "nickname");
        user.setLastUpdatedDate(LocalDate.now().minusDays(1));

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInfoDto result = userService.updatePassword(login, oldPassword, newPassword);

        assertEquals(encodedNewPassword, user.getPassword());
        assertEquals(LocalDate.now(), user.getLastUpdatedDate());

        verify(userRepository).findByLogin(login);
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_WrongOldPassword_ShouldThrowException() {
        String login = "testUser";
        String oldPassword = "wrongPass";
        String newPassword = "newPass";

        User user = new User(login, "encodedOldPass", "email@test.com", "nickname");

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        assertThrows(UserIncorrectPasswordException.class, () ->
                userService.updatePassword(login, oldPassword, newPassword));

        verify(userRepository).findByLogin(login);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updatePassword_UserNotFound_ShouldThrowException() {
        String login = "unknownUser";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updatePassword(login, oldPassword, newPassword));

        verify(userRepository).findByLogin(login);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserNickname_Success() {
        String login = "testUser";
        String nickname = "testNickname";

        when(userRepository.findNicknameByLogin(login)).thenReturn(Optional.of(nickname));

        Map<String, String> result = userService.getUserNickname(login);

        assertEquals(1, result.size());
        assertEquals(nickname, result.get("nickname"));

        verify(userRepository).findNicknameByLogin(login);
    }

    @Test
    void getUserNickname_UserNotFound_ShouldThrowException() {
        String login = "unknownUser";

        when(userRepository.findNicknameByLogin(login)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.getUserNickname(login));

        verify(userRepository).findNicknameByLogin(login);
    }

}