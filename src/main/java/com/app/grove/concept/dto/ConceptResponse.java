package com.app.grove.concept.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ConceptResponse {

    private String id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> prerequisiteIds;
    private List<String> tagIds;
}
