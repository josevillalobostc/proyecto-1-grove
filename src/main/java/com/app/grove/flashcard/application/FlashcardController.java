package com.app.grove.flashcard.application;

import com.app.grove.flashcard.domain.FlashcardService;
import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
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

    @PostMapping
    public ResponseEntity<FlashcardResponse> create(@Valid @RequestBody FlashcardRequest request){
        FlashcardResponse response=flashcardService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<FlashcardResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(flashcardService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponse> findById(@PathVariable String id){
        FlashcardResponse response=flashcardService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Page<FlashcardResponse>> findByDifficulty(
            @PathVariable Integer difficulty,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(flashcardService.findByDifficulty(difficulty, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id){
        flashcardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
