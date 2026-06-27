package com.app.grove.flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Flashcard response enriched with the concept it belongs to,
 * and SRS (Spaced Repetition) tracking fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardStudyResponse {
    private String id;
    private String front;
    private String back;
    private String hint;
    private Integer difficulty;
    private LocalDateTime createdAt;

    /** The concept this flashcard is linked to */
    private String conceptId;
    private String conceptTitle;
    /** Tag/cluster of the parent concept (e.g. "COMPUTER SCIENCE") */
    private String conceptTag;

    /** SRS scheduling fields */
    private Integer interval;      // days until next review
    private Double easeFactor;     // SRS ease multiplier
    private LocalDateTime nextReviewAt;
    private int reviewCount;
}
