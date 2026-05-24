package com.app.grove.comment.infrastructure;

import com.app.grove.comment.domain.Comment;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends Neo4jRepository<Comment, String> {

    List<Comment> findByConcept(Concept concept);
    List<Comment> findByConceptAndParentCommentIsNull(Concept concept);
}
