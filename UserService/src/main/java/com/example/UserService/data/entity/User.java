package com.example.UserService.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends TimeBasedEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(min = 4, max = 30)
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @NotBlank
    @Length(max = 100)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Length(max = 50)
    @Email
    @Column(name = "email",nullable = false, unique = true)
    private String email;

    @Column(name = "nickname")
    @Length(max = 100)
    @NotBlank
    private String nickname;
}
