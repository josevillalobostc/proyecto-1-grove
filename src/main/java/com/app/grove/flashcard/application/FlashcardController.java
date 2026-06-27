package com.app.grove.flashcard.application;

import com.app.grove.flashcard.domain.FlashcardService;
import com.app.grove.flashcard.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    // ─── Basic CRUD ───────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<FlashcardResponse> create(@Valid @RequestBody FlashcardRequest request) {
        return new ResponseEntity<>(flashcardService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<FlashcardResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(flashcardService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(flashcardService.findById(id));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Page<FlashcardResponse>> findByDifficulty(
            @PathVariable Integer difficulty,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(flashcardService.findByDifficulty(difficulty, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        flashcardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Study sessions (SRS) ────────────────────────────────────────────────

    /**
     * Returns the current user's study session (all due flashcards, ordered by priority).
     * Used to populate the Flashcards study page (SESSION PROGRESS header).
     * GET /api/v1/flashcards/session
     */
    @GetMapping("/session")
    public ResponseEntity<StudySessionResponse> getStudySession() {
        return ResponseEntity.ok(flashcardService.getStudySession());
    }

    /**
     * Returns a study session filtered to flashcards of a specific concept.
     * GET /api/v1/flashcards/session/concept/{conceptId}
     */
    @GetMapping("/session/concept/{conceptId}")
    public ResponseEntity<StudySessionResponse> getSessionByConcept(
            @PathVariable String conceptId) {
        return ResponseEntity.ok(flashcardService.getSessionByConcept(conceptId));
    }

    /**
     * Submits a review rating for a flashcard (SM-2 SRS).
     * Body: { "flashcardId": "...", "rating": 3 }
     * Ratings: 1=Again, 2=Hard, 3=Good, 4=Easy  (matching the mockup buttons)
     * POST /api/v1/flashcards/review
     */
    @PostMapping("/review")
    public ResponseEntity<FlashcardStudyResponse> review(
            @Valid @RequestBody FlashcardReviewRequest request) {
        return ResponseEntity.ok(flashcardService.reviewFlashcard(request));
    }
}
