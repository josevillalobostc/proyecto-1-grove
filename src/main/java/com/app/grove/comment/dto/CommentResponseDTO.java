package com.app.grove.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private String id;
    private String text;
    private LocalDateTime createdAt;
    private String authorId;
    private String authorName;
    private String conceptId;
    private String parentCommentId;
}
