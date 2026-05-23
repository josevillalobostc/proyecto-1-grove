package com.app.grove.flashcard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;

@Node("Flashcard")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Flashcard {
    @Id @GeneratedValue
    private String id;
    private String front;
    private String back;
    private String hint;
    private Integer difficulty;
    private LocalDateTime createdAt;



}
