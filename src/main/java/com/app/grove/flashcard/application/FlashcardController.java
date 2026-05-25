package com.app.grove.flashcard.application;

import com.app.grove.flashcard.domain.FlashcardService;
import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @PostMapping
    public ResponseEntity<FlashcardResponse> create(@RequestBody FlashcardRequest request) {
        FlashcardResponse response = flashcardService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FlashcardResponse>> getAll() {
        return ResponseEntity.ok(flashcardService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(flashcardService.findById(id));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<FlashcardResponse>> getByDifficulty(@PathVariable Integer difficulty) {
        return ResponseEntity.ok(flashcardService.findByDifficulty(difficulty));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlashcardResponse>> searchByFront(@RequestParam String keyword) {
        return ResponseEntity.ok(flashcardService.searchByFront(keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        flashcardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
