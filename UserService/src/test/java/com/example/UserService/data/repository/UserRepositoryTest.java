package com.example.UserService.data.repository;

import com.example.UserService.data.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLogin("testUser");
        user.setEmail("testuser@example.com");
        user.setNickname("TestNickname");
        user.setPassword("password123");

        user.setCreatedDate(LocalDate.now());
        user.setLastUpdatedDate(LocalDate.now());

        userRepository.save(user);
    }

    @Test
    void testFindByLogin() {
        Optional<User> foundUser = userRepository.findByLogin("testUser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLogin()).isEqualTo("testUser");
    }

    @Test
    void testFindNicknameByLogin() {
        Optional<String> nickname = userRepository.findNicknameByLogin("testUser");

        assertThat(nickname).isPresent();
        assertThat(nickname.get()).isEqualTo("TestNickname");
    }

    @Test
    void testExistsByLogin() {
        boolean exists = userRepository.existsByLogin("testUser");

        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail("testuser@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByLoginNotExist() {
        boolean exists = userRepository.existsByLogin("nonExistingUser");

        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByEmailNotExist() {
        boolean exists = userRepository.existsByEmail("nonexisting@example.com");

        assertThat(exists).isFalse();
    }
}
