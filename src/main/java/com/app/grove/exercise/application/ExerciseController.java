package com.app.grove.exercise.application;

import com.app.grove.exercise.domain.ExerciseService;
import com.app.grove.exercise.dto.ExerciseRequest;
import com.app.grove.exercise.dto.ExerciseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest request) {
        ExerciseResponse response = exerciseService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ExerciseResponse>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(exerciseService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable String id) {
        ExerciseResponse response = exerciseService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<ExerciseResponse>> findByType(
            @PathVariable String type,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(exerciseService.findByType(type, pageable));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Page<ExerciseResponse>> getByDifficulty(
            @PathVariable Integer difficulty,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(exerciseService.findByDifficulty(difficulty, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ExerciseResponse>> searchByQuestion(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(exerciseService.findByQuestionContaining(keyword, pageable));
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<Page<ExerciseResponse>> getByUserId(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(exerciseService.findExerciseByUserId(userId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        exerciseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
