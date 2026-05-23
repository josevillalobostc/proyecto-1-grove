package com.app.grove.concept.domain;

import com.app.grove.tag.domain.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Concept {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Relationship(
        type = "PREREQUISITE",
        direction = Relationship.Direction.OUTGOING
    )
    private List<Concept> prerequisites;

    @Relationship(
        type = "TAGGED_AS",
        direction = Relationship.Direction.OUTGOING
    )
    private List<Tag> tags;

    @Relationship(
        type = "HAS_FLASHCARD",
        direction = Relationship.Direction.OUTGOING
    )
    private List<Flashcard> flashcards;

    @Relationship(
        type = "HAS_EXERCISE",
        direction = Relationship.Direction.OUTGOING
    )
    private List<Exercise> exercises;
}
