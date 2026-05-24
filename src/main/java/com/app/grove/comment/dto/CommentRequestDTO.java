package com.app.grove.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotBlank(message = "El texto del comentario no puede estar vacío")
    private String text;

    @NotNull(message = "El ID del autor es obligatorio")
    private String authorId;

    @NotNull(message = "El ID del concepto es obligatorio")
    private String conceptId;

    private String parentCommentId;
}
