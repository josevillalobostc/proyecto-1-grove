package com.app.grove.concept.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node
@Getter
@Setter
@NoArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String prompt;
    private String solution;
}
