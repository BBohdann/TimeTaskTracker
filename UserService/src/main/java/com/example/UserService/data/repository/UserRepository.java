package com.example.UserService.data.repository;

import com.example.UserService.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    @Query("SELECT u.nickname FROM User u WHERE u.login = :login")
    Optional<String> findNicknameByLogin(@Param("login") String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}
