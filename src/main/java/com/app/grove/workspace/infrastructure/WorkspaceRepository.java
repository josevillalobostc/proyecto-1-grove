package com.app.grove.workspace.infrastructure;

import com.app.grove.workspace.domain.Workspace;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface WorkspaceRepository extends Neo4jRepository<Workspace, String> {
    Workspace findByName(String name);

    @Query("MATCH (u:User {id: $userId})-[:MEMBER_OF]->(w:Workspace) RETURN w")
    List<Workspace> findByMemberId(@Param("userId") String userId);
}
