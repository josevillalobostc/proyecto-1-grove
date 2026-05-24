package com.app.grove.comment.domain;

import com.app.grove.comment.dto.CommentRequestDTO;
import com.app.grove.comment.dto.CommentResponseDTO;
import com.app.grove.comment.infrastructure.CommentRepository;
import com.app.grove.events.NewCommentNotificationEvent;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ConceptRepository conceptRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO request) {

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

        Concept concept = conceptRepository.findById(request.getConceptId())
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado"));

        Comment comment = modelMapper.map(request, Comment.class);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setConcept(concept);

        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comentario padre no encontrado"));
            comment.setParentComment(parent);
        }

        Comment saved = commentRepository.save(comment);

        Concept concept = conceptRepository.findById(request.getConceptId()).orElseThrow();
        String conceptAuthorId = concept.getCreatedBy() != null ? concept.getCreatedBy().getId() : null;
        if (conceptAuthorId != null && !conceptAuthorId.equals(request.getAuthorId())) {
            eventPublisher.publishEvent(new NewCommentNotificationEvent(
                    concept.getId(),
                    concept.getTitle(),
                    request.getText(),
                    author.getUsername(),
                    conceptAuthorId
            ));
        }
        return convertToResponse(saved);
    }

    @Transactional
    public List<CommentResponseDTO> getCommentsByConcept(String conceptId) {
        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado"));

        List<Comment> comments = commentRepository.findByConcept(concept);
        return comments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CommentResponseDTO> getRootCommentsByConcept(String conceptId) {
        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado"));

        List<Comment> rootComments = commentRepository.findByConceptAndParentCommentIsNull(concept);
        return rootComments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(String commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comentario no encontrado");
        }
        commentRepository.deleteById(commentId);
    }

    private CommentResponseDTO convertToResponse(Comment comment) {
        CommentResponseDTO response = modelMapper.map(comment, CommentResponseDTO.class);
        if (comment.getAuthor() != null) {
            response.setAuthorId(comment.getAuthor().getId());
            response.setAuthorName(comment.getAuthor().getUsername());
        }

        if (comment.getConcept() != null) {
            response.setConceptId(comment.getConcept().getId());
        }

        if (comment.getParentComment() != null) {
            response.setParentCommentId(comment.getParentComment().getId());
        }

        return response;
    }

}
