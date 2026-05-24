package com.app.grove.workspace.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class WorkspaceResponse {

    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private List<String> conceptIds;
    private List<String> memberIds;
}
