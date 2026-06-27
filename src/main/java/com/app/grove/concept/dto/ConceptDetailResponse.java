package com.app.grove.concept.dto;

import com.app.grove.tag.dto.TagResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Rich concept detail response including full tag objects, connection count,
 * and prerequisite/dependent concept summaries.
 * Used in the node detail panel shown in the mockup (right-side panel).
 */
@Data
public class ConceptDetailResponse {
    private String id;
    private String title;
    private String content;
    private String createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String workspaceId;
    private String forkedFromId;

    /** Full tag objects (id, name, color) */
    private List<TagResponse> tags;

    /** IDs of prerequisite concepts */
    private List<String> prerequisiteIds;

    /** Titles of prerequisite concepts (for easy display) */
    private List<String> prerequisiteTitles;

    /** Total number of edges (incoming + outgoing) in the graph */
    private int connectionCount;

    /** User's confidence level for this concept (0-100, null if not set) */
    private Integer confidenceLevel;
}
