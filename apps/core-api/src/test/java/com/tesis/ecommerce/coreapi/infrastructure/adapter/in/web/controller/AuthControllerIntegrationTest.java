package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.controller;

import com.tesis.ecommerce.coreapi.AbstractIntegrationTest;
import com.tesis.ecommerce.coreapi.application.usecase.RegisterUseCase;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AuthLoginRequest;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AuthRegisterRequest;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Tag("integration")
class AuthControllerIntegrationTest extends AbstractIntegrationTest {
    private static final String TEST_PASSWORD = "test-password-fixture";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegisterSuccess() throws Exception {
        AuthRegisterRequest request = AuthRegisterRequest.builder()
            .email("newuser@example.com")
            .password(TEST_PASSWORD)
            .name("New User")
            .build();

        mockMvc.perform(post("/api/v1/auth/register")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token", notNullValue()))
            .andExpect(jsonPath("$.user.email", notNullValue()));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // First register a user
        AuthRegisterRequest registerRequest = AuthRegisterRequest.builder()
            .email("testuser@example.com")
            .password(TEST_PASSWORD)
            .name("Test User")
            .build();

        mockMvc.perform(post("/api/v1/auth/register")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // Then login
        AuthLoginRequest loginRequest = AuthLoginRequest.builder()
            .email("testuser@example.com")
            .password(TEST_PASSWORD)
            .build();

        mockMvc.perform(post("/api/v1/auth/login")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()));
    }
}
