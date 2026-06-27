package com.app.grove.flashcard.domain;

import com.app.grove.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;

/**
 * Tracks a user's SRS (Spaced Repetition System) progress for a specific flashcard.
 * Implements the SM-2 algorithm as reflected in the Flashcards study mockup
 * (Again / Hard / Good / Easy ratings with scheduled intervals).
 *
 * Stored as a relationship node: (:User)-[:HAS_PROGRESS]->(:UserFlashcardProgress)->[:FOR_FLASHCARD]->(:Flashcard)
 */
@Node("UserFlashcardProgress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFlashcardProgress {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    @Relationship(type = "REVIEWS", direction = Relationship.Direction.INCOMING)
    private User user;

    @Relationship(type = "FOR_FLASHCARD", direction = Relationship.Direction.OUTGOING)
    private Flashcard flashcard;

    /** SM-2 ease factor (default 2.5) */
    private double easeFactor = 2.5;

    /** Current review interval in days */
    private int interval = 0;

    /** Number of times reviewed */
    private int reviewCount = 0;

    /** When this card is next due for review */
    private LocalDateTime nextReviewAt;

    /** Last review timestamp */
    private LocalDateTime lastReviewedAt;
}
