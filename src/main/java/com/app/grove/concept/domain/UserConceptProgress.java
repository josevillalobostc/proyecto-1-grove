package com.app.grove.concept.domain;

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

/**
 * Tracks a user's confidence level for a specific concept.
 * Enables the "Niveles de confianza" feature and the Overall Progress view.
 *
 * Scale: 0 = Not started, 1-33 = Low, 34-66 = Medium, 67-99 = High, 100 = Mastered
 * The "MASTERED" badge in the mockup corresponds to confidenceLevel == 100.
 */
@Node("UserConceptProgress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserConceptProgress {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    @Relationship(type = "TRACKS_PROGRESS", direction = Relationship.Direction.INCOMING)
    private User user;

    @Relationship(type = "FOR_CONCEPT", direction = Relationship.Direction.OUTGOING)
    private Concept concept;

    /** 0-100 confidence score set by the user */
    private int confidenceLevel;

    /** Human-readable label derived from confidenceLevel */
    private String status; // "NOT_STARTED", "LEARNING", "REVIEWING", "MASTERED"
}
