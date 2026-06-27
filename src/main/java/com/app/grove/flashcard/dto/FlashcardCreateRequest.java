package com.app.grove.flashcard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request to create a flashcard linked to a specific concept.
 * Replaces the old FlashcardRequest when creating via /concepts/{id}/flashcards.
 */
@Data
public class FlashcardCreateRequest {
    @NotBlank(message = "La parte frontal no puede estar vacía")
    private String front;

    @NotBlank(message = "La parte trasera no puede estar vacía")
    private String back;

    private String hint;

    /** 1=easy, 2=medium, 3=hard */
    private Integer difficulty;
}
