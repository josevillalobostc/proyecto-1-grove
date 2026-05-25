package com.app.grove.comment.application;

import com.app.grove.comment.domain.CommentService;
import com.app.grove.comment.dto.CommentRequestDTO;
import com.app.grove.comment.dto.CommentResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(@Valid @RequestBody CommentRequestDTO request) {
        CommentResponseDTO created = commentService.createComment(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/concept/{conceptId}")
    public ResponseEntity<Page<CommentResponseDTO>> getByConcept(
            @PathVariable String conceptId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByConcept(conceptId, pageable));
    }

    @GetMapping("/concept/{conceptId}/root")
    public ResponseEntity<Page<CommentResponseDTO>> getRootComments(
            @PathVariable String conceptId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(commentService.getRootCommentsByConcept(conceptId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
