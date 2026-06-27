package com.app.grove.concept.infrastructure;

import com.app.grove.concept.domain.UserConceptProgress;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConceptProgressRepository extends Neo4jRepository<UserConceptProgress, String> {

    @Query("""
        MATCH (u:user {id: $userId})-[:TRACKS_PROGRESS]->(p:UserConceptProgress)-[:FOR_CONCEPT]->(c:Concept {id: $conceptId})
        RETURN p, u, c
        """)
    Optional<UserConceptProgress> findByUserIdAndConceptId(
            @Param("userId") String userId,
            @Param("conceptId") String conceptId
    );

    @Query("""
        MATCH (u:user {id: $userId})-[:TRACKS_PROGRESS]->(p:UserConceptProgress)-[:FOR_CONCEPT]->(c:Concept)
        RETURN p, u, c
        """)
    List<UserConceptProgress> findAllByUserId(@Param("userId") String userId);
}
