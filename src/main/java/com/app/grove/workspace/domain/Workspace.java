package com.app.grove.workspace.domain;

import com.app.grove.concept.domain.Concept;
import com.app.grove.user.domain.User;

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
public class Workspace {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String name;
    private String description;
    private boolean isPublic;
    private LocalDateTime createdAt;

    @Relationship(type = "BELONGS_TO",direction = Relationship.Direction.INCOMING)
    private List<Concept> concepts;

    @Relationship(type = "MEMBER_OF",direction = Relationship.Direction.INCOMING)
    private List<User> members;

    @Relationship(type = "CREATED_BY", direction = Relationship.Direction.OUTGOING)
    private User createdBy;
}
