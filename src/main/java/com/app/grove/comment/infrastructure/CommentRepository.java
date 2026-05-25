package com.app.grove.comment.infrastructure;

import com.app.grove.comment.domain.Comment;
import com.app.grove.concept.domain.Concept;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends Neo4jRepository<Comment, String> {

    Page<Comment> findByConcept(Concept concept, Pageable pageable);
    Page<Comment> findByConceptAndParentCommentIsNull(Concept concept, Pageable pageable);
}
