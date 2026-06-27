package com.app.grove.concept.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Full graph payload returned to the frontend for D3.js / Cytoscape.js rendering.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphResponseDTO {
    private List<GraphNodeDTO> nodes;
    private List<GraphEdgeDTO> edges;
}
