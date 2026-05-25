package com.app.grove.exercise.domain;

import com.app.grove.concept.domain.Concept;
import com.app.grove.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Node("Exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    @Id @GeneratedValue
    private String id;
    private String question;
    private String answer;
    private String explanation;
    private String type;
    private List<String> options;
    private Integer difficulty;
    private LocalDateTime createdAt;

    //Relaciones
    @Relationship(type="HAS_CONCEPT",direction=Relationship.Direction.OUTGOING)
    private List<Concept> concepts;

    @Relationship(type="BELONGS_TO",direction=Relationship.Direction.OUTGOING)
    private User user;

}
