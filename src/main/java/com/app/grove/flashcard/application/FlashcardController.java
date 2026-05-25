package com.app.grove.flashcard.application;

import com.app.grove.flashcard.domain.FlashcardService;
import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<FlashcardResponse>> getAll(){
        List<FlashcardResponse> flashcards=flashcardService.getAll();
        return ResponseEntity.ok(flashcards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponse> findById(@PathVariable String id){
        FlashcardResponse response=flashcardService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("difficulty/{difficulty}")
    public ResponseEntity<List<FlashcardResponse>> findByDifficulty(Integer difficulty){
        List<FlashcardResponse> flashcards=flashcardService.findByDifficulty(difficulty);
        return ResponseEntity.ok(flashcards);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id){
        flashcardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
