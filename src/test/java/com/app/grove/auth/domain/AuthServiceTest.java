package com.app.grove.auth.domain;

import com.app.grove.auth.dto.SignInRequest;
import com.app.grove.auth.dto.SignUpRequest;
import com.app.grove.auth.dto.TokenResponse;
import com.app.grove.exceptions.UserAlreadyExistsException;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.dto.UserResponse;
import com.app.grove.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager, new ModelMapper(), eventPublisher);
    }

    @Test
    void shouldSignUpWhenCredentialsAreUnique() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("secret");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId("u1");
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        savedUser.setPassword("encoded");
        savedUser.setRole(Role.ROLE_USER);

        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        UserResponse response = authService.signUp(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("u1");
        assertThat(response.getUsername()).isEqualTo("newuser");
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("existing");
        request.setEmail("existing@example.com");
        request.setPassword("secret");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> authService.signUp(request))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("Ya existe un usuario con dicho username");
    }

    @Test
    void shouldSignInWhenCredentialsAreValid() {
        SignInRequest request = new SignInRequest();
        request.setUsername("validuser");
        request.setPassword("secret");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        User account = new User();
        account.setId("u1");
        account.setUsername("validuser");
        account.setRole(Role.ROLE_USER);

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(account));
        when(jwtService.generateToken(account)).thenReturn("jwt-token");

        TokenResponse response = authService.signIn(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }
}
