package com.app.grove.exercise.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.exercise.infrastructure.ExerciseRepository;
import com.app.grove.exercise.dto.ExerciseRequest;
import com.app.grove.exercise.dto.ExerciseResponse;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ModelMapper modelMapper;

    public ExerciseResponse create(ExerciseRequest request){
        Exercise exercise= modelMapper.map(request,Exercise.class);
        exercise.setCreatedAt(LocalDateTime.now());
        Exercise saved = exerciseRepository.save(exercise);
        return modelMapper.map(saved,ExerciseResponse.class);
    }

    public Page<ExerciseResponse> getAll(Pageable pageable) {
        return exerciseRepository.findAll(pageable).map(ex -> modelMapper.map(ex, ExerciseResponse.class));
    }

    public ExerciseResponse findById(String id){
        Exercise exercise=exerciseRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Ejercicio no encontrado con id "+id));
        return modelMapper.map(exercise,ExerciseResponse.class);
    }

    public Page<ExerciseResponse> findByType(String type, Pageable pageable) {
        return exerciseRepository.findByType(type, pageable).map(ex -> modelMapper.map(ex, ExerciseResponse.class));
    }

    public Page<ExerciseResponse> findByDifficulty(Integer difficulty, Pageable pageable) {
        return exerciseRepository.findByDifficulty(difficulty, pageable).map(ex -> modelMapper.map(ex, ExerciseResponse.class));
    }

    public Page<ExerciseResponse> findByQuestionContaining(String keyword, Pageable pageable) {
        return exerciseRepository.findByQuestionContaining(keyword, pageable).map(ex -> modelMapper.map(ex, ExerciseResponse.class));
    }

    public Page<ExerciseResponse> findExerciseByUserId(String userId, Pageable pageable) {
        return exerciseRepository.findExerciseByUserId(userId, pageable).map(ex -> modelMapper.map(ex, ExerciseResponse.class));
    }

    public void deleteById(String id){
        if(!exerciseRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar: El ejercicio no existe"); }
        exerciseRepository.deleteById(id);
    }

}
