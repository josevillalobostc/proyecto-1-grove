package com.app.grove.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {

    @NotBlank(message = "El nombre de la etiqueta es obligatorio")
    private String name;

    private String description;
    private String color;
}
