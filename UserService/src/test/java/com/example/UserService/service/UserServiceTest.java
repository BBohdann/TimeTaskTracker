package com.example.UserService.service;

import com.example.UserService.data.entity.User;
import com.example.UserService.data.repository.UserRepository;
import com.example.UserService.service.exception.EmailAlreadyExistException;
import com.example.UserService.service.exception.UserAlreadyExistException;
import com.example.UserService.service.exception.UserNotFoundException;
import com.example.UserService.service.dto.RegisterUserDto;
import com.example.UserService.service.dto.UpdatePasswordDto;
import com.example.UserService.service.dto.UserDto;
import com.example.UserService.service.dto.UserTokenData;
import com.example.UserService.service.exception.UserIncorrectPasswordException;
import com.example.UserService.service.mapper.UserMapper;
import com.example.UserService.service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private static final Long USER_ID = 1L;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(USER_ID);
        existingUser.setLogin("original-login");
        existingUser.setEmail("original@example.com");
        existingUser.setNickname("original-nick");
        existingUser.setPassword("encoded-old-password");
    }

    @Nested
    class RegisterUser {

        @Test
        void encodesPasswordAndSavesWhenLoginAndEmailFree() {
            RegisterUserDto dto = new RegisterUserDto();
            dto.setLogin("new-login");
            dto.setEmail("new@example.com");
            dto.setPassword("raw-password");

            when(userRepository.existsByLogin("new-login")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            User mappedEntity = new User();
            when(userMapper.registerUserDtoToUserEntity(dto)).thenReturn(mappedEntity);
            when(encoder.encode("raw-password")).thenReturn("encoded-password");

            userService.registerUser(dto);

            assertThat(mappedEntity.getPassword()).isEqualTo("encoded-password");
            verify(userRepository).save(mappedEntity);
        }

        @Test
        void throwsUserAlreadyExistWhenLoginTaken() {
            RegisterUserDto dto = new RegisterUserDto();
            dto.setLogin("taken-login");
            dto.setEmail("new@example.com");

            when(userRepository.existsByLogin("taken-login")).thenReturn(true);

            assertThatThrownBy(() -> userService.registerUser(dto))
                    .isInstanceOf(UserAlreadyExistException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        void throwsEmailAlreadyExistWhenEmailTaken() {
            RegisterUserDto dto = new RegisterUserDto();
            dto.setLogin("free-login");
            dto.setEmail("taken@example.com");

            when(userRepository.existsByLogin("free-login")).thenReturn(false);
            when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.registerUser(dto))
                    .isInstanceOf(EmailAlreadyExistException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateLogin {

        @Test
        void updatesLoginAndReturnsTokenDataWhenFree() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByLogin("new-login")).thenReturn(false);

            UserTokenData result = userService.updateLogin(USER_ID, "new-login");

            assertThat(existingUser.getLogin()).isEqualTo("new-login");
            assertThat(result.getId()).isEqualTo(USER_ID);
            assertThat(result.getLogin()).isEqualTo("new-login");
        }

        @Test
        void throwsUserAlreadyExistWhenLoginTaken() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByLogin("taken-login")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateLogin(USER_ID, "taken-login"))
                    .isInstanceOf(UserAlreadyExistException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        void throwsUserNotFoundWhenUserMissing() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateLogin(USER_ID, "new-login"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    class UpdateEmail {

        @Test
        void updatesEmailAndSavesWhenFree() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

            userService.updateEmail(USER_ID, "new@example.com");

            assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
        }

        @Test
        void throwsEmailAlreadyExistWhenTaken() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateEmail(USER_ID, "taken@example.com"))
                    .isInstanceOf(EmailAlreadyExistException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateNickname {

        @Test
        void updatesNicknameAndSaves() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));

            userService.updateNickname(USER_ID, "new-nickname");

            assertThat(existingUser.getNickname()).isEqualTo("new-nickname");
        }
    }

    @Nested
    class UpdatePassword {

        @Test
        void encodesAndSavesNewPasswordWhenOldPasswordMatches() {
            UpdatePasswordDto dto = new UpdatePasswordDto();
            dto.setId(USER_ID);
            dto.setOldPassword("old-raw");
            dto.setNewPassword("new-raw");

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(encoder.matches("old-raw", "encoded-old-password")).thenReturn(true);
            when(encoder.encode("new-raw")).thenReturn("encoded-new-password");

            userService.updatePassword(dto);

            assertThat(existingUser.getPassword()).isEqualTo("encoded-new-password");
        }

        @Test
        void throwsIncorrectPasswordWhenOldPasswordWrong() {
            UpdatePasswordDto dto = new UpdatePasswordDto();
            dto.setId(USER_ID);
            dto.setOldPassword("wrong-raw");

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(encoder.matches("wrong-raw", "encoded-old-password")).thenReturn(false);

            assertThatThrownBy(() -> userService.updatePassword(dto))
                    .isInstanceOf(UserIncorrectPasswordException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void deletesUserWhenPasswordCorrect() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(encoder.matches("correct-raw", "encoded-old-password")).thenReturn(true);

            userService.deleteUser(USER_ID, "correct-raw");

            verify(userRepository).delete(existingUser);
        }

        @Test
        void throwsIncorrectPasswordAndDoesNotDeleteWhenPasswordWrong() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(encoder.matches("wrong-raw", "encoded-old-password")).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(USER_ID, "wrong-raw"))
                    .isInstanceOf(UserIncorrectPasswordException.class);

            verify(userRepository, never()).delete(any());
        }

        @Test
        void throwsUserNotFoundWhenUserMissing() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(USER_ID, "any-password"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    class GetUser {

        @Test
        void returnsMappedDtoWhenFound() {
            UserDto expectedDto = new UserDto();
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
            when(userMapper.userEntityToUserDto(existingUser)).thenReturn(expectedDto);

            UserDto result = userService.getUser(USER_ID);

            assertThat(result).isEqualTo(expectedDto);
        }

        @Test
        void throwsUserNotFoundWhenMissing() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUser(USER_ID))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}