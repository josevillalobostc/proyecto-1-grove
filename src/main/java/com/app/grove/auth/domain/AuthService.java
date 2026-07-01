package com.app.grove.auth.domain;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.grove.auth.dto.SignInRequest;
import com.app.grove.auth.dto.SignUpRequest;
import com.app.grove.auth.dto.TokenResponse;
import com.app.grove.events.WelcomeEmailEvent;
import com.app.grove.exceptions.UserAlreadyExistsException;
import com.app.grove.exceptions.UsernameNotFoundException;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.dto.UserResponse;
import com.app.grove.user.infrastructure.UserRepository;
import com.app.grove.workspace.domain.Workspace;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserResponse signUp(SignUpRequest request){
        String username = request.getUsername();
        String email = request.getEmail();
        if(userRepository.existsByUsername(username)){
            throw new UserAlreadyExistsException("Ya existe un usuario con dicho username");
        }

        if(userRepository.existsByEmail(email)){
            throw new UserAlreadyExistsException("Ya existe un usuario con dicho email");
        }


        User account = new User();
        account.setUsername(request.getUsername());
        account.setEmail((request.getEmail()));
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.ROLE_USER);
        account.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(account);

        // Auto-create a personal workspace for every new user
        Workspace personal = new Workspace();
        personal.setName(savedUser.getUsername() + "'s Workspace");
        personal.setDescription("Espacio personal de " + savedUser.getUsername());
        personal.setPublic(false);
        personal.setCreatedAt(java.time.LocalDateTime.now());
        personal.setCreatedBy(savedUser);
        personal.setMembers(new java.util.ArrayList<>(java.util.List.of(savedUser)));
        workspaceRepository.save(personal);

        eventPublisher.publishEvent(new WelcomeEmailEvent(
                savedUser.getEmail(),
                savedUser.getUsername(),
                "Grove"
        ));

        return modelMapper.map(savedUser, UserResponse.class);
    }

    public TokenResponse signIn(SignInRequest request){
        String username = request.getUsername();
        String password = request.getPassword();

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)

        );

        User account = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        String token = jwtService.generateToken(account);
        TokenResponse response = new TokenResponse(token);
        return response;
    }
}
