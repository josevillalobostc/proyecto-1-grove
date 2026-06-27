package com.app.grove.concept.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a directed edge in the knowledge graph.
 * source -> target means "target REQUIRES source" (source is prerequisite of target).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphEdgeDTO {
    /** The prerequisite concept id (the "required" side) */
    private String source;
    /** The concept id that depends on the source */
    private String target;
}
