package com.app.grove.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotBlank(message = "El texto del comentario no puede estar vacío")
    private String content;

    @NotNull(message = "El ID del concepto es obligatorio")
    private String conceptId;

    private String parentId;
}
