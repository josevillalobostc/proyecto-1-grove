package com.app.grove.flashcard.dto;

import lombok.Data;

import java.util.List;

/**
 * Response for a study session start.
 * Contains the ordered list of flashcards to review and session metadata.
 */
@Data
public class StudySessionResponse {
    /** Total flashcards in this session */
    private int total;
    /** Flashcards ordered by SRS priority (due first) */
    private List<FlashcardStudyResponse> flashcards;
}
