package com.app.grove.comment.infrastructure;

import com.app.grove.AbstractNeo4jTest;
import com.app.grove.comment.domain.Comment;
import com.app.grove.concept.domain.Concept;
import com.app.grove.user.domain.Role;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindCommentsByConceptAndFilterRootComments() {
        User author = new User();
        author.setUsername("commenter");
        author.setEmail("commenter@example.com");
        author.setPassword("password");
        author.setRole(Role.ROLE_USER);
        author.setCreatedAt(LocalDateTime.now());
        userRepository.save(author);

        Concept concept = new Concept();
        concept.setTitle("Neo4j Patterns");
        concept.setContent("Graph database concepts");
        concept.setCreatedAt(LocalDateTime.now());
        concept.setUpdatedAt(LocalDateTime.now());

        Comment rootComment = new Comment();
        rootComment.setText("Great concept");
        rootComment.setCreatedAt(LocalDateTime.now());
        rootComment.setAuthor(author);
        rootComment.setConcept(concept);

        Comment replyComment = new Comment();
        replyComment.setText("I agree");
        replyComment.setCreatedAt(LocalDateTime.now());
        replyComment.setAuthor(author);
        replyComment.setConcept(concept);
        replyComment.setParentComment(rootComment);

        commentRepository.save(rootComment);
        commentRepository.save(replyComment);

        var pageable = PageRequest.of(0, 10);
        var allComments = commentRepository.findByConcept_Id(concept.getId(), pageable).getContent();
        Page<Comment> rootComments = commentRepository.findByConcept_IdAndParentCommentIsNull(concept.getId(), PageRequest.of(0, 10));

        assertThat(allComments).hasSize(2);
        assertThat(rootComments.getContent()).hasSize(1);
        assertThat(rootComments.getContent().get(0).getText()).isEqualTo("Great concept");
    }
}
