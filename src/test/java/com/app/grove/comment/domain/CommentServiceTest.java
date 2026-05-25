package com.app.grove.comment.domain;

import com.app.grove.comment.dto.CommentRequestDTO;
import com.app.grove.comment.dto.CommentResponseDTO;
import com.app.grove.comment.infrastructure.CommentRepository;
import com.app.grove.concept.domain.Concept;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConceptRepository conceptRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(
            commentRepository,
            userRepository,
            conceptRepository,
            new ModelMapper()
        );
    }

    @Test
    void shouldCreateCommentWhenAuthorAndConceptExist() {
        User author = new User();
        author.setId("u1");
        author.setUsername("commenter");

        Concept concept = new Concept();
        concept.setId("c1");
        concept.setTitle("Graph Node");

        CommentRequestDTO request = new CommentRequestDTO();
        request.setText("Nice post");
        request.setAuthorId("u1");
        request.setConceptId("c1");

        when(userRepository.findById("u1")).thenReturn(Optional.of(author));
        when(conceptRepository.findById("c1")).thenReturn(Optional.of(concept));
        when(commentRepository.save(org.mockito.ArgumentMatchers.any(Comment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponseDTO response = commentService.createComment(request);

        assertThat(response).isNotNull();
        assertThat(response.getText()).isEqualTo("Nice post");
        assertThat(response.getAuthorId()).isEqualTo("u1");
        assertThat(response.getConceptId()).isEqualTo("c1");
    }

    @Test
    void shouldThrowWhenParentCommentDoesNotExist() {
        User author = new User();
        author.setId("u1");

        Concept concept = new Concept();
        concept.setId("c1");

        CommentRequestDTO request = new CommentRequestDTO();
        request.setText("Reply");
        request.setAuthorId("u1");
        request.setConceptId("c1");
        request.setParentCommentId("missing");

        when(userRepository.findById("u1")).thenReturn(Optional.of(author));
        when(conceptRepository.findById("c1")).thenReturn(Optional.of(concept));
        when(commentRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Comentario padre no encontrado");
    }
}
