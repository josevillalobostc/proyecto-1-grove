package com.app.grove.comment.domain;

import com.app.grove.comment.dto.CommentRequestDTO;
import com.app.grove.comment.dto.CommentResponseDTO;
import com.app.grove.comment.infrastructure.CommentRepository;
import com.app.grove.concept.domain.Concept;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ConceptRepository conceptRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        commentService = new CommentService(
            commentRepository,
            userRepository,
            conceptRepository,
            new ModelMapper()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateCommentWhenAuthorAndConceptExist() {
        User author = new User();
        author.setId("u1");
        author.setUsername("commenter");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(author);

        Concept concept = new Concept();
        concept.setId("c1");
        concept.setTitle("Graph Node");

        CommentRequestDTO request = new CommentRequestDTO();
        request.setContent("This is a new comment");
        request.setConceptId("c1");

        when(userRepository.findById("u1")).thenReturn(Optional.of(author));
        when(conceptRepository.findById("c1")).thenReturn(Optional.of(concept));
        when(commentRepository.save(org.mockito.ArgumentMatchers.any(Comment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponseDTO response = commentService.createComment(request);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("This is a new comment");
        assertThat(response.getAuthorId()).isEqualTo("u1");
        assertThat(response.getConceptId()).isEqualTo("c1");
    }

    @Test
    void shouldThrowWhenParentCommentDoesNotExist() {
        User author = new User();
        author.setId("u1");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(author);

        Concept concept = new Concept();
        concept.setId("c1");

        CommentRequestDTO request = new CommentRequestDTO();
        request.setContent("Reply comment");
        request.setConceptId("c1");
        request.setParentId("missing");

        when(userRepository.findById("u1")).thenReturn(Optional.of(author));
        when(conceptRepository.findById("c1")).thenReturn(Optional.of(concept));
        when(commentRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Comentario padre no encontrado");
    }
}
