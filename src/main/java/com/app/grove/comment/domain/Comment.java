package com.app.grove.comment.domain;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("Comment")
public class Comment {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    private String text;
    private LocalDateTime createdAt;


    @Relationship(type = "COMMENTED_BY", direction = Relationship.Direction.OUTGOING)
    private User author;

    @Relationship(type = "ON_CONCEPT", direction = Relationship.Direction.OUTGOING)
    private Concept concept;

    @Relationship(type = "REPLIES_TO", direction = Relationship.Direction.OUTGOING)
    private Comment parentComment;
}
