package com.app.grove.exercise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseRequest {
    @NotBlank(message="La pregunta no puede estar vacía o llena de espacios")
    private String question;

    private String answer;
    private String explanation;

    @Pattern(regexp="^(multiple_choice|true_false)$",message="El tipo solo puede ser 'multiple_choice' o 'true_false'")
    private String type;

    private List<String> options;
    private Integer difficulty;

}
