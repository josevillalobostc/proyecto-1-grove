package com.app.grove.workspace.infrastructure;

import com.app.grove.user.domain.User;
import com.app.grove.workspace.domain.Workspace;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface WorkspaceRepository extends Neo4jRepository<Workspace, String> {
    Workspace findByName(String name);

    @Query(
        value = "MATCH (u:User {id: $userId})-[:MEMBER_OF]->(w:Workspace) RETURN w",
        countQuery = "MATCH (u:User {id: $userId})-[:MEMBER_OF]->(w:Workspace) RETURN count(w)"
    )
    Page<Workspace> findByMemberId(@Param("userId") String userId, Pageable pageable);

    Page<Workspace> findByIsPublicTrue(Pageable pageable);

    @Query("MATCH (u:User)-[:MEMBER_OF]->(w:Workspace {id: $id}) RETURN u")
    public List<User> findMembersById(String id);
}
