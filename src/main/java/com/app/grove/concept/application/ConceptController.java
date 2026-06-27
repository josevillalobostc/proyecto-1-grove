package com.app.grove.concept.application;

import com.app.grove.concept.domain.ConceptService;
import com.app.grove.concept.dto.*;
import com.app.grove.flashcard.dto.FlashcardCreateRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/concepts")
@RequiredArgsConstructor
public class ConceptController {

    private final ConceptService conceptService;

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ConceptResponse> createConcept(
        @Valid @RequestBody ConceptRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conceptService.createConcept(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConceptResponse> getConcept(@PathVariable String id) {
        return ResponseEntity.ok(conceptService.getConceptById(id));
    }

    /**
     * Rich concept detail including full tags, connection count and user confidence level.
     * Used by the right-side node detail panel in the Knowledge Graph view.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<ConceptDetailResponse> getConceptDetail(@PathVariable String id) {
        return ResponseEntity.ok(conceptService.getConceptDetail(id));
    }

    @GetMapping
    public ResponseEntity<Page<ConceptResponse>> getAllConcepts(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(conceptService.getAllConcepts(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConceptResponse> updateConcept(
        @PathVariable String id,
        @Valid @RequestBody ConceptUpdateRequest request
    ) {
        return ResponseEntity.ok(conceptService.updateConcept(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcept(@PathVariable String id) {
        conceptService.deleteConcept(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/fork")
    public ResponseEntity<ConceptResponse> forkConcept(
        @PathVariable String id,
        @RequestParam String targetWorkspaceId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            conceptService.forkConcept(id, targetWorkspaceId)
        );
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    /**
     * Global search across title, content and tag names.
     * Use ?keyword=xxx  — powers the main search bar.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ConceptResponse>> search(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(conceptService.searchGlobal(keyword, pageable));
    }

    // ─── Graph data ───────────────────────────────────────────────────────────

    /**
     * Returns the full graph (nodes + edges) for the public workspace.
     * Default view for the Knowledge Graph page when no workspace filter is active.
     * GET /api/v1/concepts/graph/public
     */
    @GetMapping("/graph/public")
    public ResponseEntity<GraphResponseDTO> getPublicGraph() {
        return ResponseEntity.ok(conceptService.getPublicGraph());
    }

    /**
     * Returns the full graph (nodes + edges) for a specific workspace.
     * GET /api/v1/concepts/graph?workspaceId={id}
     */
    @GetMapping("/graph")
    public ResponseEntity<GraphResponseDTO> getGraphByWorkspace(
            @RequestParam String workspaceId) {
        return ResponseEntity.ok(conceptService.getGraphByWorkspace(workspaceId));
    }

    /**
     * Returns a local neighborhood subgraph centered on a concept.
     * Called when the user clicks a node to see its local connections.
     * GET /api/v1/concepts/{id}/graph
     */
    @GetMapping("/{id}/graph")
    public ResponseEntity<GraphResponseDTO> getNeighborhoodGraph(@PathVariable String id) {
        return ResponseEntity.ok(conceptService.getNeighborhoodGraph(id));
    }

    // ─── Cluster / Tag filtering ──────────────────────────────────────────────

    /**
     * Get all concepts belonging to a tag/cluster (by tag ID).
     * Powers "Vista de clúster" feature.
     * GET /api/v1/concepts/cluster?tagId={id}
     */
    @GetMapping("/cluster")
    public ResponseEntity<Page<ConceptResponse>> getByCluster(
            @RequestParam String tagId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(conceptService.getConceptsByTag(tagId, pageable));
    }

    /**
     * Get concepts by tag name (e.g. ?tagName=Algebra).
     * GET /api/v1/concepts/cluster/by-name?tagName={name}
     */
    @GetMapping("/cluster/by-name")
    public ResponseEntity<Page<ConceptResponse>> getByClusterName(
            @RequestParam String tagName,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(conceptService.getConceptsByTagName(tagName, pageable));
    }

    // ─── Learning paths ───────────────────────────────────────────────────────

    /**
     * Returns a topologically ordered list of concepts for a workspace.
     * Concepts with no prerequisites come first (depth=0).
     * Powers "Rutas de aprendizaje guiadas" feature.
     * GET /api/v1/concepts/learning-path?workspaceId={id}
     */
    @GetMapping("/learning-path")
    public ResponseEntity<List<ConceptResponse>> getLearningPath(
            @RequestParam String workspaceId) {
        return ResponseEntity.ok(conceptService.getLearningPath(workspaceId));
    }

    // ─── Prerequisites ────────────────────────────────────────────────────────

    @PostMapping("/{id}/prerequisites/{prereqId}")
    public ResponseEntity<ConceptResponse> addPrerequisite(
        @PathVariable String id,
        @PathVariable String prereqId
    ) {
        return ResponseEntity.ok(conceptService.addPrerequisite(id, prereqId));
    }

    @DeleteMapping("/{id}/prerequisites/{prereqId}")
    public ResponseEntity<ConceptResponse> removePrerequisite(
        @PathVariable String id,
        @PathVariable String prereqId
    ) {
        return ResponseEntity.ok(conceptService.removePrerequisite(id, prereqId));
    }

    /**
     * Returns all transitive prerequisites for a concept (full chain, ordered by depth).
     * Powers the "Visualización de prerrequisitos" feature.
     * GET /api/v1/concepts/{id}/prerequisites
     */
    @GetMapping("/{id}/prerequisites")
    public ResponseEntity<List<ConceptResponse>> getAllPrerequisites(@PathVariable String id) {
        return ResponseEntity.ok(conceptService.getAllPrerequisites(id));
    }

    // ─── Related concepts ─────────────────────────────────────────────────────

    /**
     * Returns concepts sharing at least one tag with the given concept.
     * Powers the "RELATED BRANCHES" section in the node detail panel.
     * GET /api/v1/concepts/{id}/related
     */
    @GetMapping("/{id}/related")
    public ResponseEntity<List<ConceptResponse>> getRelatedConcepts(@PathVariable String id) {
        return ResponseEntity.ok(conceptService.getRelatedConcepts(id));
    }

    // ─── Tags ─────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/tags/{tagId}")
    public ResponseEntity<ConceptResponse> addTag(
        @PathVariable String id,
        @PathVariable String tagId
    ) {
        return ResponseEntity.ok(conceptService.addTagToConcept(id, tagId));
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public ResponseEntity<ConceptResponse> removeTag(
        @PathVariable String id,
        @PathVariable String tagId
    ) {
        return ResponseEntity.ok(conceptService.removeTagFromConcept(id, tagId));
    }

    // ─── Flashcards (nested) ──────────────────────────────────────────────────

    /**
     * Creates a flashcard and links it to this concept.
     * POST /api/v1/concepts/{id}/flashcards
     */
    @PostMapping("/{id}/flashcards")
    public ResponseEntity<FlashcardResponse> addFlashcard(
        @PathVariable String id,
        @Valid @RequestBody FlashcardCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            conceptService.addFlashcardToConcept(id, request)
        );
    }

    /**
     * Returns all flashcards linked to this concept.
     * GET /api/v1/concepts/{id}/flashcards
     */
    @GetMapping("/{id}/flashcards")
    public ResponseEntity<List<FlashcardResponse>> getFlashcards(@PathVariable String id) {
        return ResponseEntity.ok(conceptService.getFlashcardsForConcept(id));
    }

    // ─── Confidence levels ────────────────────────────────────────────────────

    /**
     * Sets the authenticated user's confidence level for this concept.
     * Body: { "confidenceLevel": 75 }
     * Returns the updated concept detail (including the new status badge).
     * PUT /api/v1/concepts/{id}/confidence
     */
    @PutMapping("/{id}/confidence")
    public ResponseEntity<ConceptDetailResponse> setConfidence(
        @PathVariable String id,
        @RequestBody Map<String, Integer> body
    ) {
        int level = body.getOrDefault("confidenceLevel", 0);
        return ResponseEntity.ok(conceptService.setConfidenceLevel(id, level));
    }
}
