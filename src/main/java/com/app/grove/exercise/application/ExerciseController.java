package com.app.grove.exercise.application;

import com.app.grove.exercise.domain.ExerciseService;
import com.app.grove.exercise.dto.ExerciseRequest;
import com.app.grove.exercise.dto.ExerciseResponse;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest request) {
        ExerciseResponse response = exerciseService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAll(){
        List<ExerciseResponse> exercises = exerciseService.getAll();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable String id) {
        ExerciseResponse response = exerciseService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ExerciseResponse>> findByType(@PathVariable String type){
        List<ExerciseResponse> exercises = exerciseService.findByType(type);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<ExerciseResponse>> getByDifficulty(@PathVariable Integer difficulty){
        List<ExerciseResponse> exercises = exerciseService.findByDifficulty(difficulty);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExerciseResponse>> searchByQuestion(@RequestParam String keyword) {
        List<ExerciseResponse> exercises = exerciseService.findByQuestionContaining(keyword);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<ExerciseResponse>> getByUserId(@PathVariable String userId){
        List<ExerciseResponse> exercises = exerciseService.findExerciseByUserId(userId);
        return ResponseEntity.ok(exercises);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        exerciseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
