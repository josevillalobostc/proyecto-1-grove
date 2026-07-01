package com.app.grove.admin.application;

import com.app.grove.config.DataInitializer;
import com.app.grove.exceptions.ForbiddenException;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final Neo4jClient neo4jClient;
    private final DataInitializer dataInitializer;

    @PostMapping("/reset")
    public ResponseEntity<String> resetServer() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Acceso denegado: Se requiere rol de administrador.");
        }

        // Wipe the database
        neo4jClient.query("MATCH (n) DETACH DELETE n").run();

        // Run data initializer again to restore default configuration
        dataInitializer.run();

        return ResponseEntity.ok("Servidor reiniciado y configuración por defecto restaurada.");
    }
}
