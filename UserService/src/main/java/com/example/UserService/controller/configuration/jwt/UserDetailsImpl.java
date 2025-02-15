package com.example.UserService.controller.configuration.jwt;

import com.example.UserService.data.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String login;
    private Long id;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String token;

    public UserDetailsImpl(Long id, String login, String password, String token) {
        this.login = login;
        this.id = id;
        this.password = password;
        this.token = token;
    }

    public UserDetailsImpl(Long id, String login, String password) {
        this.login = login;
        this.id = id;
        this.password = password;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(user.getId(), user.getLogin(), user.getPassword());
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
