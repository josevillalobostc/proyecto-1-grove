package com.app.grove.concept.domain;

import com.app.grove.concept.dto.ConceptRequest;
import com.app.grove.concept.dto.ConceptResponse;
import com.app.grove.concept.dto.ConceptUpdateRequest;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.BadRequestException;
import com.app.grove.exceptions.ForbiddenException;
import com.app.grove.exceptions.InvalidOperationException;
import com.app.grove.events.ConceptCreatedNotificationEvent;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.tag.domain.Tag;
import com.app.grove.tag.infrastructure.TagRepository;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.workspace.domain.Workspace;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final TagRepository tagRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ConceptResponse createConcept(ConceptRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User creator = (User) auth.getPrincipal();

        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado"));

        if (!workspace.isPublic()) {
            boolean isMember = workspace.getMembers() != null && workspace.getMembers().stream()
                    .anyMatch(m -> m.getId().equals(creator.getId()));
            if (!isMember) {
                throw new ForbiddenException("No eres miembro de este workspace privado.");
            }
        }

        Concept concept = new Concept();
        concept.setCreatedBy(creator);
        concept.setTitle(request.getTitle());
        concept.setContent(request.getContent());
        concept.setCreatedAt(LocalDateTime.now());
        concept.setWorkspace(workspace);
        concept = conceptRepository.save(concept);

        if (workspace.getConcepts() != null) {
            workspace.getConcepts().add(concept);
            workspaceRepository.save(workspace);
        }

        eventPublisher.publishEvent(new ConceptCreatedNotificationEvent(
                concept.getId(),
                concept.getTitle(),
                workspace.getId(),
                creator.getId()
        ));
        return mapToResponse(concept);
    }

    @Transactional
    public ConceptResponse forkConcept(String originalConceptId, String targetWorkspaceId) {
        Concept original = conceptRepository
            .findById(originalConceptId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Concepto original no encontrado")
            );

        if (original.getWorkspace().getId().equals(targetWorkspaceId)) {
            throw new InvalidOperationException("No puedes copiar un concepto al mismo workspace.");
        }

        Workspace targetWorkspace = workspaceRepository
            .findById(targetWorkspaceId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Workspace destino no encontrado")
            );

        Concept fork = new Concept();
        fork.setTitle(original.getTitle());
        fork.setContent(original.getContent());
        fork.setCreatedAt(LocalDateTime.now());
        fork.setWorkspace(targetWorkspace);
        fork.setForkedFrom(original);
        fork = conceptRepository.save(fork);
        return mapToResponse(fork);
    }

    public ConceptResponse getConceptById(String id) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));
        return mapToResponse(concept);
    }

    public Page<ConceptResponse> getAllConcepts(Pageable pageable) {
        return conceptRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional
    public ConceptResponse updateConcept(String id, ConceptUpdateRequest request) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (!concept.getCreatedBy().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el creador del concepto o un administrador pueden modificarlo.");
        }

        if (request.getTitle() != null) concept.setTitle(request.getTitle());
        if (request.getContent() != null) concept.setContent(request.getContent());
        concept.setUpdatedAt(LocalDateTime.now());
        concept = conceptRepository.save(concept);
        return mapToResponse(concept);
    }

    @Transactional
    public void deleteConcept(String id) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));
        conceptRepository.delete(concept);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (!concept.getCreatedBy().getId().equals(currentUser.getId())
                && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el creador del concepto o un administrador pueden eliminarlo.");
        }
    }

    @Transactional
    public ConceptResponse addPrerequisite(String conceptId, String prerequisiteId) {
        if (conceptId.equals(prerequisiteId)) {
            throw new InvalidOperationException("Un concepto no puede ser prerrequisito de sí mismo.");
        }

        if (conceptRepository.existsPathBetween(prerequisiteId, conceptId)) {
            throw new InvalidOperationException(
                "Agregar este prerrequisito crearía un ciclo en el grafo de conocimiento."
            );
        }

        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));
        Concept prerequisite = conceptRepository.findById(prerequisiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Prerrequisito no encontrado: " + prerequisiteId));

        if (!concept.getPrerequisites().contains(prerequisite)) {
            concept.getPrerequisites().add(prerequisite);
            concept = conceptRepository.save(concept);
        }
        return mapToResponse(concept);
    }

    @Transactional
    public ConceptResponse removePrerequisite(String conceptId, String prerequisiteId) {
        Concept concept = conceptRepository
            .findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));
        concept
            .getPrerequisites()
            .removeIf(p -> p.getId().equals(prerequisiteId));
        concept = conceptRepository.save(concept);
        return mapToResponse(concept);
    }

    @Transactional
    public ConceptResponse addTagToConcept(String conceptId, String tagId) {
        Concept concept = conceptRepository
            .findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));

        Tag tag = tagRepository
            .findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Tag no encontrado: " + tagId));
        if (!concept.getTags().contains(tag)) {
            concept.getTags().add(tag);
            concept = conceptRepository.save(concept);
        }
        return mapToResponse(concept);
    }

    @Transactional
    public ConceptResponse removeTagFromConcept(String conceptId, String tagId) {
        Concept concept = conceptRepository
            .findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));

        concept.getTags().removeIf(t -> t.getId().equals(tagId));
        concept = conceptRepository.save(concept);
        return mapToResponse(concept);
    }

    public Page<ConceptResponse> searchByTitle(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("El término de búsqueda no puede estar vacío.");
        }
        return conceptRepository.searchByTitleContaining(keyword, pageable).map(this::mapToResponse);
    }

    private ConceptResponse mapToResponse(Concept concept) {
        ConceptResponse response = modelMapper.map(concept, ConceptResponse.class);
        response.setWorkspaceId(
            concept.getWorkspace() != null
                ? concept.getWorkspace().getId()
                : null
        );
        response.setForkedFromId(
            concept.getForkedFrom() != null
                ? concept.getForkedFrom().getId()
                : null
        );
        response.setCreatedById(
            concept.getCreatedBy() != null
                ? concept.getCreatedBy().getId()
                : null
        );
        if (concept.getPrerequisites() != null) {
            response.setPrerequisiteIds(
                concept
                    .getPrerequisites()
                    .stream()
                    .map(Concept::getId)
                    .collect(Collectors.toList())
            );
        } else {
            response.setPrerequisiteIds(List.of());
        }
        if (concept.getTags() != null) {
            response.setTagIds(
                concept
                    .getTags()
                    .stream()
                    .map(Tag::getId)
                    .collect(Collectors.toList())
            );
        } else {
            response.setTagIds(List.of());
        }
        return response;
    }
}
