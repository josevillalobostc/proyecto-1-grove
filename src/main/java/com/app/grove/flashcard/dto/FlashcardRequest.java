package com.app.grove.flashcard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FlashcardRequest {
    @NotBlank(message="La parte frontal no puede quedar en blanco")
    private String front;
    @NotBlank(message="La parte trasera no puede quedar en blanco")
    private String back;

    private String hint;

    @Pattern(regexp="^[1-3]$",message="La dificultad debe ser un número del 1 al 3")
    private Integer difficulty;

}
