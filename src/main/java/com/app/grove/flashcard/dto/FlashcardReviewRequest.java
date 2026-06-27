package com.app.grove.flashcard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Payload for reviewing a flashcard (spaced repetition answer).
 * The 'rating' maps to standard SRS buttons:
 *   1 = Again (< 1m)
 *   2 = Hard   (shown in mockup)
 *   3 = Good   (shown in mockup)
 *   4 = Easy   (shown in mockup, "7D" interval)
 */
@Data
public class FlashcardReviewRequest {
    @NotBlank(message = "El id de la flashcard es obligatorio")
    private String flashcardId;

    @Min(value = 1, message = "El rating mínimo es 1 (Again)")
    @Max(value = 4, message = "El rating máximo es 4 (Easy)")
    private int rating;
}
