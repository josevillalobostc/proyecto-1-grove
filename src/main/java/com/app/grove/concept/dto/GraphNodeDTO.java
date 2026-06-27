package com.app.grove.concept.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a single node in the knowledge graph visualization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphNodeDTO {
    private String id;
    private String title;
    private String content;
    /** Tag names attached to this concept (for cluster coloring) */
    private List<String> tags;
    /** Tag IDs for filtering */
    private List<String> tagIds;
    private String workspaceId;
    /** Number of incoming + outgoing prerequisite relationships */
    private int connectionCount;
}
