package com.app.grove.workspace.infrastructure;

import com.app.grove.AbstractNeo4jTest;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import com.app.grove.workspace.domain.Workspace;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WorkspaceRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        workspaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldQueryWorkspacesAndMembersCorrectly() {
        User user = new User();
        user.setUsername("workspaceuser");
        user.setEmail("workspaceuser@example.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        Workspace publicWorkspace = new Workspace();
        publicWorkspace.setName("Public Workspace");
        publicWorkspace.setDescription("Public workspace for everyone");
        publicWorkspace.setPublic(true);
        publicWorkspace.setCreatedAt(LocalDateTime.now());
        publicWorkspace.setMembers(List.of(user));

        Workspace privateWorkspace = new Workspace();
        privateWorkspace.setName("Private Workspace");
        privateWorkspace.setDescription("Private workspace");
        privateWorkspace.setPublic(false);
        privateWorkspace.setCreatedAt(LocalDateTime.now());
        privateWorkspace.setMembers(List.of(user));

        workspaceRepository.saveAll(List.of(publicWorkspace, privateWorkspace));

        Workspace loaded = workspaceRepository.findByName("Public Workspace");
        assertThat(loaded).isNotNull();
        assertThat(loaded.getName()).isEqualTo("Public Workspace");

        List<Workspace> publicWorkspaces = workspaceRepository.findByIsPublicTrue();
        assertThat(publicWorkspaces).hasSize(1);
        assertThat(publicWorkspaces.get(0).getName()).isEqualTo("Public Workspace");

        List<Workspace> memberWorkspaces = workspaceRepository.findByMemberId(user.getId());
        assertThat(memberWorkspaces).hasSize(2);

        List<User> members = workspaceRepository.findMembersByWorkspaceId(publicWorkspace.getId());
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUsername()).isEqualTo("workspaceuser");
    }
}
