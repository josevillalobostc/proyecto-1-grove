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
        System.out.println("DEBUG: Starting resetServer");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        System.out.println("DEBUG: Checked auth");
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Acceso denegado: Se requiere rol de administrador.");
        }

        System.out.println("DEBUG: Spawning async thread to wipe DB");
        new Thread(() -> {
            try {
                System.out.println("DEBUG: About to wipe DB");
                neo4jClient.query("MATCH (n) DETACH DELETE n").run();
                System.out.println("DEBUG: Wiped DB, running dataInitializer");
                dataInitializer.run();
                System.out.println("DEBUG: Finished dataInitializer");
            } catch (Exception e) {
                System.err.println("Error resetting server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.accepted().body("Servidor reiniciándose en segundo plano. Esto puede tomar un momento.");
    }
}
