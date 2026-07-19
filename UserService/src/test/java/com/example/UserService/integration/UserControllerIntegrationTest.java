package com.example.UserService.integration;

import com.example.UserService.controller.configuration.jwt.JwtAuthentication;
import com.example.UserService.controller.request.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
 
import java.util.Base64;
 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIntegrationTest extends BaseIntegrationTest {
    private RequestPostProcessor asUser(Long userId, String login) {
        return SecurityMockMvcRequestPostProcessors.authentication(new JwtAuthentication(userId, login));
    }
 
    @Test
    void getUser_returnsCurrentUserProfile() throws Exception {
        RegisteredUser user = registerAndLogin("iris", "iris@example.com", "password-123");
 
        mockMvc.perform(get("/api/users/me").with(asUser(user.id(), user.login())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id()))
                .andExpect(jsonPath("$.login").value("iris"))
                .andExpect(jsonPath("$.email").value("iris@example.com"))
                .andExpect(jsonPath("$.nickname").value("iris-nick"));
    }
 
    @Test
    void getUser_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
 
    @Test
    void updateUserLogin_changesLoginAndReturnsNewTokenInHeader() throws Exception {
        RegisteredUser user = registerAndLogin("jack", "jack@example.com", "password-123");
 
        UpdateLoginRequest request = new UpdateLoginRequest();
        request.setNewLogin("jack-renamed");
 
        MvcResult result = mockMvc.perform(patch("/api/users/login")
                        .with(asUser(user.id(), user.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn();

        JsonNode claims = decodeJwtPayload(result.getResponse().getHeader("Authorization"));
        org.assertj.core.api.Assertions.assertThat(claims.get("sub").asText()).isEqualTo("jack-renamed");
        org.assertj.core.api.Assertions.assertThat(claims.get("jti").asText()).isEqualTo(user.id().toString());
 
        mockMvc.perform(get("/api/users/me").with(asUser(user.id(), "jack-renamed")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("jack-renamed"));
    }

    @Test
    void updateUserLogin_whenLoginTaken_returnsConflict() throws Exception {
        registerAndLogin("John", "john@example.com", "password-123");
        RegisteredUser lena = registerAndLogin("lena", "lena@example.com", "password-123");
 
        UpdateLoginRequest request = new UpdateLoginRequest();
        request.setNewLogin("John");
 
        mockMvc.perform(patch("/api/users/login")
                        .with(asUser(lena.id(), lena.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
 
    @Test
    void updateUserPassword_changesPasswordSoOldOneStopsWorking() throws Exception {
        RegisteredUser user = registerAndLogin("mona", "mona@example.com", "old-password-123");
 
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("old-password-123");
        request.setNewPassword("new-password-456");
 
        mockMvc.perform(patch("/api/users/password")
                        .with(asUser(user.id(), user.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
 
        loginExpectingUnauthorized("mona", "old-password-123");
        loginExpectingOk("mona", "new-password-456");
    }
 
    @Test
    void updateUserPassword_withWrongOldPassword_returnsBadRequest() throws Exception {
        RegisteredUser user = registerAndLogin("nate", "nate@example.com", "correct-password");
 
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("totally-wrong-password");
        request.setNewPassword("new-password-456");
 
        mockMvc.perform(patch("/api/users/password")
                        .with(asUser(user.id(), user.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        loginExpectingOk("nate", "correct-password");
    }
 
    @Test
    void updateUserNickname_changesNickname() throws Exception {
        RegisteredUser user = registerAndLogin("olga", "olga@example.com", "password-123");
 
        UpdateNicknameRequest request = new UpdateNicknameRequest();
        request.setNewNickname("Olga the Great");
 
        mockMvc.perform(patch("/api/users/nickname")
                        .with(asUser(user.id(), user.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
 
        mockMvc.perform(get("/api/users/me").with(asUser(user.id(), user.login())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("Olga the Great"));
    }
 
    @Test
    void updateUserEmail_changesEmail() throws Exception {
        RegisteredUser user = registerAndLogin("pete", "pete@example.com", "password-123");
 
        UpdateEmailRequest request = new UpdateEmailRequest();
        request.setNewEmail("pete-new@example.com");
 
        mockMvc.perform(patch("/api/users/email")
                        .with(asUser(user.id(), user.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
 
        mockMvc.perform(get("/api/users/me").with(asUser(user.id(), user.login())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("pete-new@example.com"));
    }
 
    @Test
    void updateUserEmail_whenEmailTaken_returnsConflict() throws Exception {
        registerAndLogin("quinn", "quinn@example.com", "password-123");
        RegisteredUser rex = registerAndLogin("kate", "k8te@example.com", "password-123");
 
        UpdateEmailRequest request = new UpdateEmailRequest();
        request.setNewEmail("quinn@example.com");
 
        mockMvc.perform(patch("/api/users/email")
                        .with(asUser(rex.id(), rex.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUser_removesAccountWhenPasswordCorrect() throws Exception {
        RegisteredUser user = registerAndLogin("sara", "sara@example.com", "password-123");
 
        DeleteUserRequest request = new DeleteUserRequest();
        request.setPassword("password-123");
 
        mockMvc.perform(delete("/api/users/me")
                        .with(asUser(user.id(), user.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        loginExpectingUnauthorized("sara", "password-123");
    }

    @Test
    void deleteUser_withWrongPassword_returnsBadRequestAndDoesNotDelete() throws Exception {
        RegisteredUser user = registerAndLogin("test", "test@example.com", "password-123");
 
        DeleteUserRequest request = new DeleteUserRequest();
        request.setPassword("wrong-password");
 
        mockMvc.perform(delete("/api/users/me")
                        .with(asUser(user.id(), user.login()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        loginExpectingOk("test", "password-123");
    }
 
    private record RegisteredUser(Long id, String login) {
    }
 
    private RegisteredUser registerAndLogin(String login, String email, String password) throws Exception {
        SignupRequest signup = new SignupRequest();
        signup.setLogin(login);
        signup.setPassword(password);
        signup.setEmail(email);
        signup.setNickname(login + "-nick");
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());
 
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPassword(password);
 
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
 
        JsonNode claims = decodeJwtPayload(result.getResponse().getHeader("Authorization"));
        Long id = Long.parseLong(claims.get("jti").asText());
 
        return new RegisteredUser(id, login);
    }
 
    private void loginExpectingOk(String login, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPassword(password);
 
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
 
    private void loginExpectingUnauthorized(String login, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPassword(password);
 
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
 
    private JsonNode decodeJwtPayload(String authorizationHeader) throws Exception {
        String token = authorizationHeader.replaceFirst("^Bearer\\s+", "");
        String[] parts = token.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        return objectMapper.readTree(payloadJson);
    }
}