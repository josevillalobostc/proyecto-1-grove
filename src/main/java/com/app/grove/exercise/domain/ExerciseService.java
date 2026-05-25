package com.app.grove.exercise.domain;

import com.app.grove.concept.domain.Concept;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.exercise.dto.ExerciseRequest;
import com.app.grove.exercise.dto.ExerciseResponse;
import com.app.grove.exercise.infrastructure.ExerciseRepository;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final ConceptRepository conceptRepository;

    @Transactional
    public ExerciseResponse create(ExerciseRequest request){
        Exercise exercise = mapToEntity(request);
        exercise.setCreatedAt(LocalDateTime.now());
        Exercise saved = exerciseRepository.save(exercise);
        return mapToResponse(saved);
    }

    public List<ExerciseResponse> getAll(){
        return exerciseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExerciseResponse findById(String id){
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con id " + id));
        return mapToResponse(exercise);
    }

    private Exercise mapToEntity(ExerciseRequest request){
        Exercise exercise = new Exercise();
        exercise.setQuestion(request.getQuestion());
        exercise.setAnswer(request.getAnswer());
        exercise.setExplanation(request.getExplanation());
        exercise.setType(request.getType());
        exercise.setOptions(request.getOptions());
        exercise.setDifficulty(request.getDifficulty());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + request.getUserId()));
            exercise.setUser(user);
        }

        if (request.getConceptIds() != null && !request.getConceptIds().isEmpty()) {
            List<Concept> concepts = StreamSupport.stream(
                    conceptRepository.findAllById(request.getConceptIds()).spliterator(), false)
                    .collect(Collectors.toList());
            if (concepts.size() != request.getConceptIds().size()) {
                throw new ResourceNotFoundException("Al menos un concepto no fue encontrado");
            }
            exercise.setConcepts(concepts);
        }

        return exercise;
    }

    private ExerciseResponse mapToResponse(Exercise exercise) {
        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setQuestion(exercise.getQuestion());
        response.setAnswer(exercise.getAnswer());
        response.setExplanation(exercise.getExplanation());
        response.setType(exercise.getType());
        response.setOptions(exercise.getOptions());
        response.setDifficulty(exercise.getDifficulty());
        response.setCreatedAt(exercise.getCreatedAt());
        if (exercise.getUser() != null) {
            response.setUserId(exercise.getUser().getId());
        }
        if (exercise.getConcepts() != null) {
            response.setConceptIds(
                    exercise.getConcepts().stream().map(Concept::getId).collect(Collectors.toList())
            );
        }
        return response;
    }

    public List<ExerciseResponse> findByType(String type){
        return exerciseRepository.findByType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponse> findByDifficulty(Integer difficulty){
        return exerciseRepository.findByDifficulty(difficulty).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponse> findByQuestionContaining(String keyword){
        return exerciseRepository.findByQuestionContaining(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponse> findExerciseByUserId(String userId){
        return exerciseRepository.findExerciseByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteById(String id){
        if(!exerciseRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar: El ejercicio no existe"); }
        exerciseRepository.deleteById(id);
    }

}
