package com.app.grove.exercise.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.exercise.infrastructure.ExerciseRepository;
import com.app.grove.flashcard.dto.ExerciseRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public Exercise create(ExerciseRequest request){
        Exercise exercise=mapToEntity(request);
        exercise.setCreatedAt(LocalDateTime.now());
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getAll(){
        return exerciseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Exercise findById(String id){
        Exercise exercise=exerciseRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Ejercicio no encontrado con id "+id));
        return exercise;
    }

    public void deleteById(String id){
        if(!exerciseRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar: El ejercicio no existe"); }
        exerciseRepository.deleteById(id);
    }

}
