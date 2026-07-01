package com.app.grove.comment.infrastructure;

import com.app.grove.comment.domain.Comment;
import com.app.grove.concept.domain.Concept;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends Neo4jRepository<Comment, String> {

    Page<Comment> findByConcept_Id(String conceptId, Pageable pageable);

    @org.springframework.data.neo4j.repository.query.Query(
        value = "MATCH (c:Comment)-[r1:ON_CONCEPT]->(con:Concept {id: $conceptId}) " +
                "WHERE NOT (c)-[:REPLIES_TO]->(:Comment) " +
                "MATCH (c)-[r2:COMMENTED_BY]->(u:User) " +
                "RETURN c, collect(r1), collect(con), collect(r2), collect(u)",
        countQuery = "MATCH (c:Comment)-[:ON_CONCEPT]->(con:Concept {id: $conceptId}) " +
                     "WHERE NOT (c)-[:REPLIES_TO]->(:Comment) " +
                     "RETURN count(c)"
    )
    Page<Comment> findRootCommentsByConceptId(String conceptId, Pageable pageable);
}
