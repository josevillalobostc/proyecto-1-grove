package com.app.grove.flashcard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import com.app.grove.concept.domain.Concept;

import java.time.LocalDateTime;
import java.util.List;

@Node("Flashcard")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Flashcard {
    @Id @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;
    private String front;
    private String back;
    private String hint;
    private Integer difficulty;
    private LocalDateTime createdAt;

    //Relaciones
    @Relationship(type="HAS_FLASHCARD",direction=Relationship.Direction.OUTGOING)
    private List<Concept> concepts;

}
