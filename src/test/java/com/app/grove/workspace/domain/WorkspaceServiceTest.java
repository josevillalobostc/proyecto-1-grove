package com.app.grove.workspace.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import com.app.grove.workspace.dto.WorkspaceRequest;
import com.app.grove.workspace.dto.WorkspaceResponse;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private UserRepository userRepository;

    private WorkspaceService workspaceService;

    @BeforeEach
    void setUp() {
        workspaceService = new WorkspaceService(workspaceRepository, userRepository, new ModelMapper());
    }

    @Test
    void shouldCreateWorkspaceWhenValid() {
        WorkspaceRequest request = new WorkspaceRequest();
        request.setName("Study Group");
        request.setDescription("Workspace for study");
        request.setPublic(true);

        Workspace saved = new Workspace();
        saved.setId("w1");
        saved.setName("Study Group");
        saved.setDescription("Workspace for study");
        saved.setPublic(true);
        saved.setCreatedAt(LocalDateTime.now());

        when(workspaceRepository.save(org.mockito.ArgumentMatchers.any(Workspace.class))).thenReturn(saved);

        WorkspaceResponse response = workspaceService.createWorkspace(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("w1");
        assertThat(response.isPublic()).isTrue();
    }

    @Test
    void shouldAddMemberToWorkspaceWhenUserExists() {
        User user = new User();
        user.setId("u1");
        user.setRole(Role.ROLE_USER);

        Workspace workspace = new Workspace();
        workspace.setId("w1");
        workspace.setMembers(List.of());

        when(workspaceRepository.findById("w1")).thenReturn(Optional.of(workspace));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(workspaceRepository.save(workspace)).thenReturn(workspace);

        WorkspaceResponse response = workspaceService.addMemberToWorkspace("w1", "u1");

        assertThat(response.getMemberIds()).containsExactly("u1");
    }

    @Test
    void shouldThrowWhenWorkspaceDoesNotExist() {
        when(workspaceRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workspaceService.getWorkspaceById("missing"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Workspace no encontrado");
    }
}
