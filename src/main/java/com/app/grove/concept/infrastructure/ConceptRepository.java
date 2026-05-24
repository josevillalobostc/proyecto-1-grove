package com.app.grove.concept.infrastructure;

import com.app.grove.concept.domain.Concept;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface ConceptRepository extends Neo4jRepository<Concept, String> {
    Concept findByTitle(String title);

    @Query(
        "MATCH (c:Concept) WHERE toLower(c.title) CONTAINS toLower($keyword) RETURN c"
    )
    List<Concept> searchByTitleContaining(@Param("keyword") String keyword);

    @Query(
        "MATCH (start:Concept)-[:PREREQUISITE*]->(target:Concept) " +
            "WHERE target.id = $conceptId RETURN DISTINCT start " +
            "ORDER BY size((start)-[:PREREQUISITE*]->(target))"
    )
    List<Concept> findAllPrerequisites(@Param("conceptId") String conceptId);
}
