package com.app.grove.auth.application;

import com.app.grove.auth.domain.AuthService;
import com.app.grove.auth.dto.SignInRequest;
import com.app.grove.auth.dto.SignUpRequest;
import com.app.grove.auth.dto.TokenResponse;
import com.app.grove.exceptions.GlobalExceptionHandler;
import com.app.grove.user.dto.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new AuthController(authService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRegisterUserWhenRequestIsValid() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("jdoe");
        request.setEmail("jdoe@example.com");
        request.setPassword("StrongPass123");

        UserResponse response = new UserResponse();
        response.setId("u1");
        response.setUsername("jdoe");
        response.setEmail("jdoe@example.com");

        when(authService.signUp(any(SignUpRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("u1"))
            .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() throws Exception {
        SignInRequest request = new SignInRequest();
        request.setUsername("jdoe");
        request.setPassword("password123");

        TokenResponse response = new TokenResponse("jwt-token");
        when(authService.signIn(any(SignInRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}
