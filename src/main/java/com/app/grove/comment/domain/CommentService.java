package com.app.grove.comment.domain;

import com.app.grove.comment.dto.CommentRequestDTO;
import com.app.grove.comment.dto.CommentResponseDTO;
import com.app.grove.comment.infrastructure.CommentRepository;
import com.app.grove.concept.domain.Concept;
import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ForbiddenException;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.user.domain.User;
import com.app.grove.user.infrastructure.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ConceptRepository conceptRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        User author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado"));

        Concept concept = conceptRepository.findById(request.getConceptId())
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado"));

        Comment comment = new Comment();
        comment.setText(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setConcept(concept);

        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comentario padre no encontrado"));
            comment.setParentComment(parent);
        }

        Comment saved = commentRepository.save(comment);
        return convertToResponse(saved);
    }

    public Page<CommentResponseDTO> getCommentsByConcept(String conceptId, Pageable pageable) {
        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado"));
        return commentRepository.findByConcept_Id(conceptId, pageable).map(this::convertToResponse);
    }

    public Page<CommentResponseDTO> getRootCommentsByConcept(String conceptId, Pageable pageable) {
        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado"));
        return commentRepository.findRootCommentsByConceptId(conceptId, pageable).map(this::convertToResponse);
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
        response.setContent(comment.getText());
        if (comment.getAuthor() != null) {
            response.setAuthorId(comment.getAuthor().getId());
            response.setAuthorUsername(comment.getAuthor().getUsername());
        }

        if (comment.getConcept() != null) {
            response.setConceptId(comment.getConcept().getId());
        }

        if (comment.getParentComment() != null) {
            response.setParentId(comment.getParentComment().getId());
        }

        return response;
    }

}
