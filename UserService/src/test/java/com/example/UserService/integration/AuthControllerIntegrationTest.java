package com.example.UserService.integration;

import com.example.UserService.controller.request.LoginRequest;
import com.example.UserService.controller.request.SignupRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;
 
import java.util.Base64;
 
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
 
    @Test
    void registerUser_withValidPayload_returnsCreated() throws Exception {
        SignupRequest request = validSignup("alice", "alice@example.com");
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_storesPasswordEncodedNotPlaintext() throws Exception {
        SignupRequest request = validSignup("Ivan", "ivan@example.com");
        request.setPassword("super-secret-raw-password");
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
 
        String storedPassword = jdbcTemplate.queryForObject(
                "SELECT password FROM users WHERE login = ?", String.class, "Ivan");
 
        assertThat(storedPassword).isNotEqualTo("super-secret-raw-password");
        assertThat(storedPassword).startsWith("$2");
    }
 
    @Test
    void registerUser_withBlankLogin_returnsBadRequest() throws Exception {
        SignupRequest request = validSignup("carol", "carol@example.com");
        request.setLogin("");
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void registerUser_withInvalidEmail_returnsBadRequest() throws Exception {
        SignupRequest request = validSignup("dave", "not-an-email");
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void registerUser_whenLoginAlreadyTaken_returnsConflict() throws Exception {
        registerUser("erin", "erin@example.com");
 
        SignupRequest duplicateLogin = validSignup("erin", "different@example.com");
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateLogin)))
                .andExpect(status().isConflict());
    }
 
    @Test
    void registerUser_whenEmailAlreadyTaken_returnsConflict() throws Exception {
        registerUser("frank", "frank@example.com");
 
        SignupRequest duplicateEmail = validSignup("frank2", "frank@example.com");
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmail)))
                .andExpect(status().isConflict());
    }
 
    @Test
    void authenticateUser_withCorrectCredentials_returnsOkWithAuthorizationHeader() throws Exception {
        registerUserWithPassword("grace", "grace@example.com", "correct-password");
 
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("grace");
        loginRequest.setPassword("correct-password");
 
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn();
 
        String authHeader = result.getResponse().getHeader("Authorization");
        assertThat(authHeader).startsWith("Bearer ");

        JsonNode claims = decodeJwtPayload(authHeader);
        assertThat(claims.get("sub").asText()).isEqualTo("grace");
        assertThat(claims.get("jti").asText()).matches("\\d+");
    }
 
    @Test
    void authenticateUser_withWrongPassword_returnsUnauthorized() throws Exception {
        registerUserWithPassword("henry", "henry@example.com", "correct-password");
 
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("henry");
        loginRequest.setPassword("wrong-password");
 
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
 
    @Test
    void authenticateUser_withUnknownLogin_returnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("nobody-registered-with-this-login");
        loginRequest.setPassword("whatever");
 
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
 
    @Test
    void authenticateUser_withBlankFields_returnsBadRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("");
        loginRequest.setPassword("");
 
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
 
    static SignupRequest validSignup(String login, String email) {
        SignupRequest request = new SignupRequest();
        request.setLogin(login);
        request.setPassword("valid-password-123");
        request.setEmail(email);
        request.setNickname(login + "-nick");
        return request;
    }
 
    private void registerUser(String login, String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignup(login, email))))
                .andExpect(status().isCreated());
    }
 
    private void registerUserWithPassword(String login, String email, String password) throws Exception {
        SignupRequest request = validSignup(login, email);
        request.setPassword(password);
 
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    static JsonNode decodeJwtPayload(String authorizationHeader) throws Exception {
        String token = authorizationHeader.replaceFirst("^Bearer\\s+", "");
        String[] parts = token.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(payloadJson);
    }
}