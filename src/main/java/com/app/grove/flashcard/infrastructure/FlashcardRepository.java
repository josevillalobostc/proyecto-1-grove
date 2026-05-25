package com.app.grove.flashcard.infrastructure;

import com.app.grove.flashcard.domain.Flashcard;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface FlashcardRepository extends Neo4jRepository<Flashcard, String> {
    List<Flashcard> findByDifficulty(Integer difficulty);
    List<Flashcard> findByFrontContaining(String keyword);
}
