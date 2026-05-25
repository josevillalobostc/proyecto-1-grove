package com.app.grove.exercise.application;

import com.app.grove.exercise.domain.ExerciseService;
import com.app.grove.exercise.dto.ExerciseRequest;
import com.app.grove.exercise.dto.ExerciseResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest request) {
        ExerciseResponse response = exerciseService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAll(){
        List<ExerciseResponse> exercises=exerciseService.getAll();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable String id) {
        ExerciseResponse response = exerciseService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ExerciseResponse>> findByType(String type){
        List<ExerciseResponse> exercises=exerciseService.findByType(type);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<ExerciseResponse>> getByDifficulty(@PathVariable Integer difficulty){
        List<ExerciseResponse> exercises=exerciseService.findByDifficulty(difficulty);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExerciseResponse>> searchByQuestion(@RequestParam String keyword) {
        List<ExerciseResponse> exercises=exerciseService.findByQuestionContaining(keyword);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<ExerciseResponse>> getByUserId(@PathVariable String userId){
        List<ExerciseResponse> exercises=exerciseService.findExerciseByUserId(userId);
        return ResponseEntity.ok(exercises);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        exerciseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
