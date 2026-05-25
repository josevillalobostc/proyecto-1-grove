package com.app.grove.flashcard.infrastructure;

import com.app.grove.exercise.domain.Exercise;
import com.app.grove.flashcard.domain.Flashcard;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends Neo4jRepository<Flashcard,String> {
    List<Exercise> findByDifficulty(Integer difficulty);

}
