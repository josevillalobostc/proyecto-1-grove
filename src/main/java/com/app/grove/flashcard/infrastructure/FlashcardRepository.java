package com.app.grove.flashcard.infrastructure;

import com.app.grove.flashcard.domain.Flashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardRepository extends Neo4jRepository<Flashcard,String> {
    Page<Flashcard> findByDifficulty(Integer difficulty, Pageable pageable);
}
