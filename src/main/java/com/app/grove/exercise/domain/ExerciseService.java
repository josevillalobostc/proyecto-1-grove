package com.app.grove.exercise.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.exercise.infrastructure.ExerciseRepository;
import com.app.grove.exercise.dto.ExerciseRequest;
import com.app.grove.exercise.dto.ExerciseResponse;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ExerciseResponse> getAll(){
        return exerciseRepository.findAll().stream()
                .map(exercise -> modelMapper.map(this,ExerciseResponse.class))
                .collect(Collectors.toList());
    }

    public ExerciseResponse findById(String id){
        Exercise exercise=exerciseRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Ejercicio no encontrado con id "+id));
        return modelMapper.map(exercise,ExerciseResponse.class);
    }

    public List<ExerciseResponse> findByType(String type){
        List<Exercise> exercises=exerciseRepository.findByType(type);
        return exercises.stream()
            .map(exercise -> modelMapper.map(this,ExerciseResponse.class))
            .collect(Collectors.toList());
    }

    public List<ExerciseResponse> findByDifficulty(Integer difficulty){
        List<Exercise> exercises=exerciseRepository.findByDifficulty(difficulty);
        return exercises.stream()
            .map(exercise -> modelMapper.map(this,ExerciseResponse.class))
            .collect(Collectors.toList());
    }

    public List<ExerciseResponse> findByQuestionContaining(String keyword){
        List<Exercise> exercises=exerciseRepository.findByQuestionContaining(keyword);
        return exercises.stream()
            .map(exercise -> modelMapper.map(this,ExerciseResponse.class))
            .collect(Collectors.toList());
    }

    public List<ExerciseResponse> findExerciseByUserId(String userId){
        List<Exercise> exercises=exerciseRepository.findExerciseByUserId(userId);
        return exercises.stream()
            .map(exercise -> modelMapper.map(this,ExerciseResponse.class))
            .collect(Collectors.toList());
    }

    public void deleteById(String id){
        if(!exerciseRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar: El ejercicio no existe"); }
        exerciseRepository.deleteById(id);
    }

}
