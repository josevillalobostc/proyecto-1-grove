package com.app.grove.concept.application;

import com.app.grove.concept.domain.ConceptService;
import com.app.grove.concept.dto.ConceptRequest;
import com.app.grove.concept.dto.ConceptResponse;
import com.app.grove.concept.dto.ConceptUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concepts")
@RequiredArgsConstructor
public class ConceptController {

    private final ConceptService conceptService;

    @PostMapping
    public ResponseEntity<ConceptResponse> createConcept(
        @Valid @RequestBody ConceptRequest request
    ) {
        ConceptResponse response = conceptService.createConcept(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConceptResponse> getConcept(@PathVariable String id) {
        return ResponseEntity.ok(conceptService.getConceptById(id));
    }

    @GetMapping
    public ResponseEntity<List<ConceptResponse>> getAllConcepts() {
        return ResponseEntity.ok(conceptService.getAllConcepts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ConceptResponse>> searchByTitle(
        @RequestParam String keyword
    ) {
        return ResponseEntity.ok(conceptService.searchByTitle(keyword));
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
        return ResponseEntity.ok(
            conceptService.removePrerequisite(id, prereqId)
        );
    }

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
        return ResponseEntity.ok(
            conceptService.removeTagFromConcept(id, tagId)
        );
    }
}
