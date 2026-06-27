package com.app.grove.flashcard.infrastructure;

import com.app.grove.flashcard.domain.UserFlashcardProgress;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFlashcardProgressRepository extends Neo4jRepository<UserFlashcardProgress, String> {

    /**
     * Find progress record for a specific user+flashcard pair.
     */
    @Query("MATCH (u:user {id: $userId})-[:REVIEWS]->(p:UserFlashcardProgress)-[:FOR_FLASHCARD]->(f:Flashcard {id: $flashcardId}) RETURN p, u, f")
    Optional<UserFlashcardProgress> findByUserIdAndFlashcardId(
            @Param("userId") String userId,
            @Param("flashcardId") String flashcardId
    );

    /**
     * Get all progress records for a user, ordered by nextReviewAt ascending
     * (due cards come first for the study session).
     */
    @Query("""
        MATCH (u:user {id: $userId})-[:REVIEWS]->(p:UserFlashcardProgress)-[:FOR_FLASHCARD]->(f:Flashcard)
        RETURN p, u, f
        ORDER BY p.nextReviewAt ASC
        """)
    List<UserFlashcardProgress> findDueFlashcardsByUserId(@Param("userId") String userId);

    /**
     * Get all progress records for a user studying a specific concept's flashcards.
     */
    @Query("""
        MATCH (concept:Concept {id: $conceptId})-[:HAS_FLASHCARD]->(f:Flashcard)
        OPTIONAL MATCH (u:user {id: $userId})-[:REVIEWS]->(p:UserFlashcardProgress)-[:FOR_FLASHCARD]->(f)
        RETURN p, u, f
        """)
    List<UserFlashcardProgress> findByUserIdAndConceptId(
            @Param("userId") String userId,
            @Param("conceptId") String conceptId
    );
}
