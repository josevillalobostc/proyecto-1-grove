package com.app.grove.workspace.application;

import com.app.grove.workspace.domain.WorkspaceService;
import com.app.grove.workspace.dto.WorkspaceRequest;
import com.app.grove.workspace.dto.WorkspaceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(
        @Valid @RequestBody WorkspaceRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            workspaceService.createWorkspace(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(
        @PathVariable String id
    ) {
        return ResponseEntity.ok(workspaceService.getWorkspaceById(id));
    }

    @GetMapping
    public ResponseEntity<Page<WorkspaceResponse>> getAllWorkspaces(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(workspaceService.getAllWorkspaces(pageable));
    }

    @GetMapping("/public")
    public ResponseEntity<Page<WorkspaceResponse>> getPublicWorkspaces(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(workspaceService.getPublicWorkspaces(pageable));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<WorkspaceResponse>> getMyWorkspaces(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(workspaceService.getMyWorkspaces(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
        @PathVariable String id,
        @Valid @RequestBody WorkspaceRequest request
    ) {
        return ResponseEntity.ok(workspaceService.updateWorkspace(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<WorkspaceResponse> addMember(
        @PathVariable String id,
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(
            workspaceService.addMemberToWorkspace(id, userId)
        );
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<WorkspaceResponse> removeMember(
        @PathVariable String id,
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(
            workspaceService.removeMemberFromWorkspace(id, userId)
        );
    }
}
