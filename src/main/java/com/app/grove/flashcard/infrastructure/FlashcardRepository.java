package com.app.grove.flashcard.infrastructure;

import com.app.grove.flashcard.domain.Flashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends Neo4jRepository<Flashcard, String> {
    Page<Flashcard> findByDifficulty(Integer difficulty, Pageable pageable);

    /**
     * Returns all flashcards attached to a given concept node via HAS_FLASHCARD.
     * Used in GET /api/v1/concepts/{id}/flashcards
     */
    @Query("""
        MATCH (c:Concept {id: $conceptId})-[:HAS_FLASHCARD]->(f:Flashcard)
        RETURN f
        """)
    List<Flashcard> findByConceptId(@Param("conceptId") String conceptId);
}
