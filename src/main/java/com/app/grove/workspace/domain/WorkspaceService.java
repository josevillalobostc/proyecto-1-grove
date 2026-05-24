package com.app.grove.workspace.domain;

import com.app.grove.concept.domain.Concept;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import com.app.grove.workspace.dto.WorkspaceRequest;
import com.app.grove.workspace.dto.WorkspaceResponse;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request) {
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setPublic(request.isPublic());
        workspace.setCreatedAt(LocalDateTime.now());
        workspace = workspaceRepository.save(workspace);
        return mapToResponse(workspace);
    }

    public WorkspaceResponse getWorkspaceById(String id) {
        Workspace workspace = workspaceRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado: " + id));
        return mapToResponse(workspace);
    }

    public List<WorkspaceResponse> getAllWorkspaces() {
        return workspaceRepository
            .findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public WorkspaceResponse updateWorkspace(String id, WorkspaceRequest request) {
        Workspace workspace = workspaceRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado: " + id));
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setPublic(request.isPublic());
        workspace = workspaceRepository.save(workspace);
        return mapToResponse(workspace);
    }

    @Transactional
    public void deleteWorkspace(String id) {
        Workspace workspace = workspaceRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado: " + id));
        workspaceRepository.delete(workspace);
    }

    @Transactional
    public WorkspaceResponse addMemberToWorkspace(String workspaceId, String userId) {
        Workspace workspace = workspaceRepository
            .findById(workspaceId)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado: " + workspaceId));

        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userId));

        if (!workspace.getMembers().contains(user)) {
            workspace.getMembers().add(user);
            workspace = workspaceRepository.save(workspace);
        }

        return mapToResponse(workspace);
    }

    @Transactional
    public WorkspaceResponse removeMemberFromWorkspace(String workspaceId, String userId) {
        Workspace workspace = workspaceRepository
            .findById(workspaceId)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado: " + workspaceId));

        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userId));

        if (workspace.getMembers().contains(user)) {
            workspace.getMembers().remove(user);
        }

        workspace = workspaceRepository.save(workspace);

        return mapToResponse(workspace);
    }

    public List<WorkspaceResponse> getPublicWorkspaces() {
        return workspaceRepository
            .findByIsPublicTrue()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private WorkspaceResponse mapToResponse(Workspace workspace) {
        WorkspaceResponse response = modelMapper.map(workspace, WorkspaceResponse.class);

        if (workspace.getConcepts() != null) {
            response.setConceptIds(
                workspace
                    .getConcepts()
                    .stream()
                    .map(Concept::getId)
                    .collect(Collectors.toList())
            );
        } else {
            response.setConceptIds(List.of());
        }
        if (workspace.getMembers() != null) {
            response.setMemberIds(
                workspace
                    .getMembers()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList())
            );
        } else {
            response.setMemberIds(List.of());
        }
        return response;
    }
}
