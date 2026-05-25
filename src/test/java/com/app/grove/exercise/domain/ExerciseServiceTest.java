package com.app.grove.exercise.domain;

import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.exercise.dto.ExerciseRequest;
import com.app.grove.exercise.dto.ExerciseResponse;
import com.app.grove.exercise.infrastructure.ExerciseRepository;
import com.app.grove.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConceptRepository conceptRepository;

    private ExerciseService exerciseService;

    @BeforeEach
    void setUp() {
        exerciseService = new ExerciseService(exerciseRepository, userRepository, conceptRepository);
    }

    @Test
    void shouldCreateExercise() {
        ExerciseRequest request = new ExerciseRequest();
        request.setQuestion("What is Java?");
        request.setAnswer("A programming language");
        request.setExplanation("Java is a compiled language");
        request.setType("multiple-choice");
        request.setOptions(List.of("Java", "Python", "Ruby"));
        request.setDifficulty(2);

        Exercise savedExercise = new Exercise();
        savedExercise.setId("e1");
        savedExercise.setQuestion(request.getQuestion());
        savedExercise.setAnswer(request.getAnswer());
        savedExercise.setExplanation(request.getExplanation());
        savedExercise.setType(request.getType());
        savedExercise.setOptions(request.getOptions());
        savedExercise.setDifficulty(request.getDifficulty());
        savedExercise.setCreatedAt(LocalDateTime.now());

        when(exerciseRepository.save(any(Exercise.class))).thenReturn(savedExercise);

        ExerciseResponse result = exerciseService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("e1");
        assertThat(result.getQuestion()).isEqualTo("What is Java?");
        assertThat(result.getCreatedAt()).isNotNull();

        ArgumentCaptor<Exercise> captor = ArgumentCaptor.forClass(Exercise.class);
        verify(exerciseRepository).save(captor.capture());
        assertThat(captor.getValue().getQuestion()).isEqualTo(request.getQuestion());
    }

    @Test
    void shouldReturnAllExercises() {
        Exercise exercise1 = new Exercise();
        exercise1.setId("e1");
        Exercise exercise2 = new Exercise();
        exercise2.setId("e2");

        when(exerciseRepository.findAll()).thenReturn(List.of(exercise1, exercise2));

        List<ExerciseResponse> result = exerciseService.getAll();

        assertThat(result).hasSize(2).extracting(ExerciseResponse::getId).containsExactly("e1", "e2");
    }

    @Test
    void shouldFindExerciseById() {
        Exercise exercise = new Exercise();
        exercise.setId("e1");
        when(exerciseRepository.findById("e1")).thenReturn(Optional.of(exercise));

        ExerciseResponse result = exerciseService.findById("e1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("e1");
    }

    @Test
    void shouldThrowWhenExerciseNotFound() {
        when(exerciseRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.findById("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ejercicio no encontrado");
    }

    @Test
    void shouldDeleteExistingExercise() {
        when(exerciseRepository.existsById("e1")).thenReturn(true);

        exerciseService.deleteById("e1");

        verify(exerciseRepository).deleteById("e1");
    }

    @Test
    void shouldThrowOnDeleteWhenNotFound() {
        when(exerciseRepository.existsById("missing")).thenReturn(false);

        assertThatThrownBy(() -> exerciseService.deleteById("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se puede eliminar");
    }
}
