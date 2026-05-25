package com.app.grove.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.grove.concept.domain.Concept;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import com.app.grove.workspace.domain.Workspace;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceRepository workspaceRepository;
    private final ConceptRepository conceptRepository;
    
    private final UserRepository userRepository;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;


    @Override
    public void run(String... args) throws Exception {
            Optional<User> existingAdmin = userRepository.findByUsername(adminName);
    
            if (existingAdmin.isEmpty()) {
                User admin = new User();
                admin.setUsername(adminName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setCreatedAt(LocalDateTime.now());
                admin.setRole(Role.ROLE_ADMIN);
                User savedAdmin = userRepository.save(admin);
    
                userRepository.save(admin);
                Workspace publicWorkspace = new Workspace();
                publicWorkspace.setName("Grove Global Community");
                publicWorkspace.setDescription("Espacio público donde todos los usuarios pueden ver conceptos base.");
                publicWorkspace.setPublic(true);
                publicWorkspace.setCreatedAt(LocalDateTime.now());
                publicWorkspace.setMembers(new ArrayList<>());
                publicWorkspace.getMembers().add(savedAdmin);

                Workspace savedWorkspace = workspaceRepository.save(publicWorkspace);
                
                Concept welcomeConcept = new Concept();
                welcomeConcept.setTitle("¿Qué es Grove?");
                welcomeConcept.setContent("Grove es una plataforma colaborativa basada en grafos de conocimiento para el aprendizaje.");
                welcomeConcept.setCreatedAt(LocalDateTime.now());
                welcomeConcept.setUpdatedAt(LocalDateTime.now());
                welcomeConcept.setWorkspace(savedWorkspace);     
                conceptRepository.save(welcomeConcept);
            }
        }
    
}