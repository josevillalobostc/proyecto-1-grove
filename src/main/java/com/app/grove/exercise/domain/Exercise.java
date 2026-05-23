package com.app.grove.exercise.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

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
    @OneToMany(mappedBy="exercise",cascade=CascadeType.ALL)
    List<Concept> concepts;

    @ManyToOne @JoinColumn(name="user_id")
    Usr user;

}
