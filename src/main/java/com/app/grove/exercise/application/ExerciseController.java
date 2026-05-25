package com.app.grove.exercise.application;

import com.app.grove.exercise.domain.Exercise;
import com.app.grove.exercise.domain.ExerciseService;
import com.app.grove.exercise.dto.ExerciseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<Exercise> create(@Valid @RequestBody ExerciseRequest request) {
        Exercise response = exerciseService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAll(){
        List<Exercise> exercises=exerciseService.getAll();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getById(@PathVariable String id) {
        Exercise response = exerciseService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Exercise>> findByType(String type){
        List<Exercise> exercises=exerciseService.findByType(type);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Exercise>> getByDifficulty(@PathVariable Integer difficulty){
        List<Exercise> exercises=exerciseService.findByDifficulty(difficulty);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Exercise>> searchByQuestion(@RequestParam String keyword) {
        List<Exercise> exercises=exerciseService.findByQuestionContaining(keyword);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<Exercise>> getByUserId(@PathVariable String userId){
        List<Exercise> exercises=exerciseService.findExerciseByUserId(userId);
        return ResponseEntity.ok(exercises);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        exerciseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
