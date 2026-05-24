package com.app.grove.concept.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConceptRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;
}
