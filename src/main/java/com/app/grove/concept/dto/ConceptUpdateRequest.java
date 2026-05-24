package com.app.grove.concept.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConceptUpdateRequest {

    @Size(max = 200)
    private String title;

    private String content;
}
