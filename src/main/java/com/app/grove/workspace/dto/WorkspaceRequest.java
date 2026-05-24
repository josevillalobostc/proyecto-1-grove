package com.app.grove.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkspaceRequest {

    @NotBlank(message = "El nombre del workspace es obligatorio")
    private String name;

    private String description;

    private boolean isPublic;
}
