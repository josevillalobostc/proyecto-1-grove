package com.app.grove.comment.application;

import com.app.grove.comment.domain.CommentService;
import com.app.grove.comment.dto.CommentRequestDTO;
import com.app.grove.comment.dto.CommentResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(@Valid @RequestBody CommentRequestDTO request) {
        CommentResponseDTO created = commentService.createComment(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/concept/{conceptId}")
    public ResponseEntity<List<CommentResponseDTO>> getByConcept(@PathVariable String conceptId) {
        return ResponseEntity.ok(commentService.getCommentsByConcept(conceptId));
    }

    @GetMapping("/concept/{conceptId}/root")
    public ResponseEntity<List<CommentResponseDTO>> getRootComments(@PathVariable String conceptId) {
        return ResponseEntity.ok(commentService.getRootCommentsByConcept(conceptId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
