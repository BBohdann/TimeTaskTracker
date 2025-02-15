package com.example.UserService.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@Table( name = "users" ,
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "login"),
                @UniqueConstraint(columnNames = "email")
        })
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
//    @Email
    @Column(name = "email",nullable = false, unique = true)
    private String email;

    @Column(name = "nickname")
    @Length(max = 100)
    @NotNull
    private String nickname;

    public User(String login, String password, String email, String nickname) {
        this.nickname = nickname;
        this.login = login;
        this.password = password;
        this.email = email;
    }
    public User() {
    }
}
