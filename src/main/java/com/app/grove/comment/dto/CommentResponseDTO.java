package com.app.grove.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private String id;
    private String content;
    private LocalDateTime createdAt;
    private String authorId;
    private String authorUsername;
    private String conceptId;
    private String parentId;
}
