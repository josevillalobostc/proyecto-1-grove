package com.app.grove.concept.domain;

import com.app.grove.concept.dto.*;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.concept.infrastructure.UserConceptProgressRepository;
import com.app.grove.exceptions.BadRequestException;
import com.app.grove.exceptions.ForbiddenException;
import com.app.grove.exceptions.InvalidOperationException;
import com.app.grove.events.ConceptCreatedNotificationEvent;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.flashcard.domain.Flashcard;
import com.app.grove.flashcard.dto.FlashcardCreateRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import com.app.grove.tag.domain.Tag;
import com.app.grove.tag.dto.TagResponse;
import com.app.grove.tag.infrastructure.TagRepository;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.workspace.domain.Workspace;
import com.app.grove.workspace.infrastructure.WorkspaceRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final TagRepository tagRepository;
    private final WorkspaceRepository workspaceRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserConceptProgressRepository progressRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = {"graphs", "learningPaths"}, allEntries = true)
    public ConceptResponse createConcept(ConceptRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User creator = (User) auth.getPrincipal();

        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado"));

        if (!workspace.isPublic()) {
            boolean isMember = workspace.getMembers() != null && workspace.getMembers().stream()
                    .anyMatch(m -> m.getId() != null && m.getId().equals(creator.getId()));
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

        eventPublisher.publishEvent(new ConceptCreatedNotificationEvent(
                concept.getId(),
                concept.getTitle(),
                workspace.getId(),
                creator.getId()
        ));
        return mapToResponse(concept);
    }

    @Transactional
    @CacheEvict(value = {"graphs", "learningPaths"}, allEntries = true)
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

    /**
     * Rich detail with full tags, connection count, and confidence level.
     * Used by the node detail panel in the Knowledge Graph mockup (right side panel).
     */
    public ConceptDetailResponse getConceptDetail(String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));

        ConceptDetailResponse detail = new ConceptDetailResponse();
        detail.setId(concept.getId());
        detail.setTitle(concept.getTitle());
        detail.setContent(concept.getContent());
        detail.setCreatedAt(concept.getCreatedAt());
        detail.setUpdatedAt(concept.getUpdatedAt());
        detail.setWorkspaceId(concept.getWorkspace() != null ? concept.getWorkspace().getId() : null);
        detail.setForkedFromId(concept.getForkedFrom() != null ? concept.getForkedFrom().getId() : null);
        detail.setCreatedById(concept.getCreatedBy() != null ? concept.getCreatedBy().getId() : null);

        // Full tag objects
        if (concept.getTags() != null) {
            detail.setTags(concept.getTags().stream()
                    .map(t -> modelMapper.map(t, TagResponse.class))
                    .collect(Collectors.toList()));
        } else {
            detail.setTags(List.of());
        }

        // Prerequisites
        if (concept.getPrerequisites() != null) {
            detail.setPrerequisiteIds(concept.getPrerequisites().stream()
                    .map(Concept::getId).collect(Collectors.toList()));
            detail.setPrerequisiteTitles(concept.getPrerequisites().stream()
                    .map(Concept::getTitle).collect(Collectors.toList()));
        } else {
            detail.setPrerequisiteIds(List.of());
            detail.setPrerequisiteTitles(List.of());
        }

        // Connection count (edges in graph)
        detail.setConnectionCount(conceptRepository.countConnections(id));

        // User confidence level
        Optional<UserConceptProgress> progress = progressRepository
                .findByUserIdAndConceptId(currentUser.getId(), id);
        detail.setConfidenceLevel(progress.map(UserConceptProgress::getConfidenceLevel).orElse(null));

        return detail;
    }

    public Page<ConceptResponse> getAllConcepts(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return conceptRepository.findAllAccessibleByUser(currentUser.getId(), pageable).map(this::mapToResponse);
    }

    @Transactional
    @CacheEvict(value = {"graphs", "learningPaths"}, allEntries = true)
    public ConceptResponse updateConcept(String id, ConceptUpdateRequest request) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        boolean isCreator = concept.getCreatedBy() != null && concept.getCreatedBy().getId() != null && concept.getCreatedBy().getId().equals(currentUser.getId());
        if (!isCreator && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el creador del concepto o un administrador pueden modificarlo.");
        }

        if (request.getTitle() != null) concept.setTitle(request.getTitle());
        if (request.getContent() != null) concept.setContent(request.getContent());
        concept.setUpdatedAt(LocalDateTime.now());
        concept = conceptRepository.save(concept);
        return mapToResponse(concept);
    }

    @Transactional
    @CacheEvict(value = {"graphs", "learningPaths"}, allEntries = true)
    public void deleteConcept(String id) {
        Concept concept = conceptRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        boolean isCreator = concept.getCreatedBy() != null && concept.getCreatedBy().getId() != null && concept.getCreatedBy().getId().equals(currentUser.getId());
        if (!isCreator && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Solo el creador del concepto o un administrador pueden eliminarlo.");
        }
        conceptRepository.delete(concept);
    }

    // ─── Prerequisites ────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = {"graphs", "learningPaths"}, allEntries = true)
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
    @CacheEvict(value = {"graphs", "learningPaths"}, allEntries = true)
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

    /** Returns all transitive prerequisites (full chain) ordered by depth. */
    public List<ConceptResponse> getAllPrerequisites(String conceptId) {
        conceptRepository.findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));
        return conceptRepository.findAllPrerequisites(conceptId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ─── Tags ─────────────────────────────────────────────────────────────────

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

    // ─── Search ───────────────────────────────────────────────────────────────

    /**
     * Global search scoped to workspaces accessible by the current user.
     */
    public Page<ConceptResponse> searchGlobal(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("El término de búsqueda no puede estar vacío.");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return conceptRepository.searchGlobal(currentUser.getId(), keyword, pageable).map(this::mapToResponse);
    }

    /** Legacy title-only search */
    public Page<ConceptResponse> searchByTitle(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("El término de búsqueda no puede estar vacío.");
        }
        return conceptRepository.searchByTitleContaining(keyword, pageable).map(this::mapToResponse);
    }

    // ─── Graph data ───────────────────────────────────────────────────────────

    /**
     * Builds the full GraphResponseDTO for a given workspace.
     * Nodes = concepts, Edges = PREREQUISITE relationships.
     * Used by the Knowledge Graph visualization (D3.js / Cytoscape.js).
     */
    @Cacheable(value = "graphs", key = "'workspace_' + #workspaceId")
    public GraphResponseDTO getGraphByWorkspace(String workspaceId) {
        workspaceRepository.findById(workspaceId)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado: " + workspaceId));

        List<Concept> concepts = conceptRepository.findAllByWorkspaceId(workspaceId);
        return buildGraph(concepts);
    }

    /**
     * Builds the graph for all public workspaces (default view when no workspace selected).
     */
    @Cacheable(value = "graphs", key = "'public'")
    public GraphResponseDTO getPublicGraph() {
        List<Concept> concepts = conceptRepository.findAllInPublicWorkspaces();
        return buildGraph(concepts);
    }

    /**
     * Builds a subgraph centered on a concept: the concept itself plus its
     * direct prerequisites and related concepts (1-hop neighborhood).
     * Used when clicking a node in the mockup to show the reduced local graph.
     */
    public GraphResponseDTO getNeighborhoodGraph(String conceptId) {
        Concept center = conceptRepository.findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));

        List<Concept> nodes = new ArrayList<>();
        nodes.add(center);
        if (center.getPrerequisites() != null) {
            nodes.addAll(center.getPrerequisites());
        }
        nodes.addAll(conceptRepository.findRelatedConcepts(conceptId));
        
        List<Concept> dependents = conceptRepository.findDependents(conceptId);
        for (Concept dep : dependents) {
            if (dep.getPrerequisites() == null) {
                dep.setPrerequisites(new ArrayList<>());
            }
            boolean exists = dep.getPrerequisites().stream()
                    .anyMatch(p -> p.getId().equals(center.getId()));
            if (!exists) {
                dep.getPrerequisites().add(center);
            }
        }
        nodes.addAll(dependents);

        return buildGraph(nodes);
    }

    private GraphResponseDTO buildGraph(List<Concept> concepts) {
        List<GraphNodeDTO> nodes = concepts.stream().map(c -> {
            GraphNodeDTO node = new GraphNodeDTO();
            node.setId(c.getId());
            node.setTitle(c.getTitle());
            node.setContent(c.getContent());
            node.setWorkspaceId(c.getWorkspace() != null ? c.getWorkspace().getId() : null);

            if (c.getTags() != null) {
                node.setTagIds(c.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
                node.setTags(c.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
            } else {
                node.setTagIds(List.of());
                node.setTags(List.of());
            }

            node.setConnectionCount(c.getPrerequisites() != null ? c.getPrerequisites().size() : 0);
            return node;
        }).collect(Collectors.toList());

        List<GraphEdgeDTO> edges = new ArrayList<>();
        for (Concept concept : concepts) {
            if (concept.getPrerequisites() != null) {
                for (Concept prereq : concept.getPrerequisites()) {
                    edges.add(new GraphEdgeDTO(prereq.getId(), concept.getId()));
                }
            }
        }

        return new GraphResponseDTO(nodes, edges);
    }

    // ─── Cluster ──────────────────────────────────────────────────────────────

    /** Get concepts belonging to a specific tag/cluster (by tag ID). */
    public Page<ConceptResponse> getConceptsByTag(String tagId, Pageable pageable) {
        tagRepository.findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Tag no encontrado: " + tagId));
        return conceptRepository.findByTagId(tagId, pageable).map(this::mapToResponse);
    }

    /** Get concepts by tag name (convenient alternative). */
    public Page<ConceptResponse> getConceptsByTagName(String tagName, Pageable pageable) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new BadRequestException("El nombre del tag no puede estar vacío.");
        }
        return conceptRepository.findByTagName(tagName, pageable).map(this::mapToResponse);
    }

    // ─── Related concepts ─────────────────────────────────────────────────────

    /** Returns concepts sharing at least one tag with the given concept. */
    public List<ConceptResponse> getRelatedConcepts(String conceptId) {
        conceptRepository.findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));
        return conceptRepository.findRelatedConcepts(conceptId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ─── Learning paths ───────────────────────────────────────────────────────

    /**
     * Returns a topologically ordered list of concepts (by prerequisite depth).
     * Concepts with no prerequisites come first; their dependents follow.
     */
    @Cacheable(value = "learningPaths", key = "#workspaceId")
    public List<ConceptResponse> getLearningPath(String workspaceId) {
        workspaceRepository.findById(workspaceId)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace no encontrado: " + workspaceId));
        return conceptRepository.findLearningPathByWorkspace(workspaceId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ─── Flashcards (nested under concept) ───────────────────────────────────

    /**
     * Creates a flashcard and links it to the given concept via HAS_FLASHCARD.
     */
    @Transactional
    public FlashcardResponse addFlashcardToConcept(String conceptId, FlashcardCreateRequest request) {
        Concept concept = conceptRepository.findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));

        Flashcard flashcard = new Flashcard();
        flashcard.setFront(request.getFront());
        flashcard.setBack(request.getBack());
        flashcard.setHint(request.getHint());
        flashcard.setDifficulty(request.getDifficulty());
        flashcard.setCreatedAt(LocalDateTime.now());
        flashcard = flashcardRepository.save(flashcard);

        if (concept.getFlashcards() == null) {
            concept.setFlashcards(new ArrayList<>());
        }
        concept.getFlashcards().add(flashcard);
        conceptRepository.save(concept);

        return modelMapper.map(flashcard, FlashcardResponse.class);
    }

    /** Returns all flashcards for a concept. */
    public List<FlashcardResponse> getFlashcardsForConcept(String conceptId) {
        conceptRepository.findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));
        return flashcardRepository.findByConceptId(conceptId)
                .stream().map(f -> modelMapper.map(f, FlashcardResponse.class))
                .collect(Collectors.toList());
    }

    // ─── Confidence levels ────────────────────────────────────────────────────

    /**
     * Sets or updates the authenticated user's confidence level for a concept.
     * 0=not started, 1-33=learning, 34-66=reviewing, 67-99=high, 100=mastered.
     */
    @Transactional
    public ConceptDetailResponse setConfidenceLevel(String conceptId, int confidenceLevel) {
        if (confidenceLevel < 0 || confidenceLevel > 100) {
            throw new BadRequestException("El nivel de confianza debe estar entre 0 y 100.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Concept concept = conceptRepository.findById(conceptId)
            .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + conceptId));

        UserConceptProgress progress = progressRepository
                .findByUserIdAndConceptId(currentUser.getId(), conceptId)
                .orElseGet(() -> {
                    UserConceptProgress p = new UserConceptProgress();
                    p.setUser(currentUser);
                    p.setConcept(concept);
                    return p;
                });

        progress.setConfidenceLevel(confidenceLevel);
        progress.setStatus(deriveStatus(confidenceLevel));
        progressRepository.save(progress);

        return getConceptDetail(conceptId);
    }

    private String deriveStatus(int level) {
        if (level == 0) return "NOT_STARTED";
        if (level <= 33) return "LEARNING";
        if (level <= 66) return "REVIEWING";
        if (level < 100) return "HIGH";
        return "MASTERED";
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

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
                concept.getPrerequisites().stream()
                    .map(Concept::getId)
                    .collect(Collectors.toList())
            );
            response.setPrerequisiteTitles(
                concept.getPrerequisites().stream()
                    .map(Concept::getTitle)
                    .collect(Collectors.toList())
            );
            response.setConnectionCount(concept.getPrerequisites().size());
        } else {
            response.setPrerequisiteIds(List.of());
            response.setPrerequisiteTitles(List.of());
            response.setConnectionCount(0);
        }
        if (concept.getTags() != null) {
            response.setTagIds(
                concept.getTags().stream()
                    .map(Tag::getId)
                    .collect(Collectors.toList())
            );
            response.setTags(
                concept.getTags().stream()
                    .map(t -> modelMapper.map(t, com.app.grove.tag.dto.TagResponse.class))
                    .collect(Collectors.toList())
            );
        } else {
            response.setTagIds(List.of());
            response.setTags(List.of());
        }
        return response;
    }
}
