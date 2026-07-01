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

    /**
     * Returns flashcards that have never been reviewed by the given user (no progress entry exists).
     * Used in the global study session to include new cards.
     */
    @Query("""
        MATCH (f:Flashcard)
        WHERE NOT (:user {id: $userId})-[:REVIEWS]->(:UserFlashcardProgress)-[:FOR_FLASHCARD]->(f)
        RETURN f
        LIMIT $limit
        """)
    List<Flashcard> findUnreviewedByUserId(@Param("userId") String userId, @Param("limit") int limit);
}
