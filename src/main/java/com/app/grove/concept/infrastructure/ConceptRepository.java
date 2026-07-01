package com.app.grove.concept.infrastructure;

import com.app.grove.concept.domain.Concept;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface ConceptRepository extends Neo4jRepository<Concept, String> {
    Concept findByTitle(String title);

    // ─── Workspace-scoped listing ─────────────────────────────────────────────

    /**
     * Returns all concepts accessible to the given user:
     * concepts in public workspaces OR private workspaces the user is a member of.
     * This enforces workspace-level privacy.
     */
    @Query(
        value = """
            MATCH (c:Concept)-[:BELONGS_TO]->(w:Workspace)
            WHERE w.isPublic = true
               OR (:user {id: $userId})-[:MEMBER_OF]->(w)
            RETURN c
            """,
        countQuery = """
            MATCH (c:Concept)-[:BELONGS_TO]->(w:Workspace)
            WHERE w.isPublic = true
               OR (:user {id: $userId})-[:MEMBER_OF]->(w)
            RETURN count(c)
            """
    )
    Page<Concept> findAllAccessibleByUser(@Param("userId") String userId, Pageable pageable);

    // ─── Search ──────────────────────────────────────────────────────────────

    /**
     * Global search scoped to accessible workspaces (public or user is a member).
     * Powers the search bar in the Knowledge Graph view.
     */
    @Query(
        value = """
            MATCH (c:Concept)-[:BELONGS_TO]->(w:Workspace)
            WHERE w.isPublic = true
               OR (:user {id: $userId})-[:MEMBER_OF]->(w)
            OPTIONAL MATCH (c)-[:TAGGED_AS]->(t:Tag)
            WITH c, collect(t.name) AS tagNames
            WHERE toLower(c.title)   CONTAINS toLower($keyword)
               OR toLower(c.content) CONTAINS toLower($keyword)
               OR ANY(tn IN tagNames WHERE toLower(tn) CONTAINS toLower($keyword))
            RETURN DISTINCT c
            """,
        countQuery = """
            MATCH (c:Concept)-[:BELONGS_TO]->(w:Workspace)
            WHERE w.isPublic = true
               OR (:user {id: $userId})-[:MEMBER_OF]->(w)
            OPTIONAL MATCH (c)-[:TAGGED_AS]->(t:Tag)
            WITH c, collect(t.name) AS tagNames
            WHERE toLower(c.title)   CONTAINS toLower($keyword)
               OR toLower(c.content) CONTAINS toLower($keyword)
               OR ANY(tn IN tagNames WHERE toLower(tn) CONTAINS toLower($keyword))
            RETURN count(DISTINCT c)
            """
    )
    Page<Concept> searchGlobal(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);

    /** Legacy title-only search (kept for backwards compat) */
    @Query(
        value = "MATCH (c:Concept) WHERE toLower(c.title) CONTAINS toLower($keyword) RETURN c",
        countQuery = "MATCH (c:Concept) WHERE toLower(c.title) CONTAINS toLower($keyword) RETURN count(c)"
    )
    Page<Concept> searchByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    // ─── Graph data ───────────────────────────────────────────────────────────

    /**
     * Returns all concepts in a workspace with their full relationships
     * loaded. Used to build the nodes list for D3/Cytoscape rendering.
     */
    @Query("""
        MATCH (c:Concept)-[:BELONGS_TO]->(w:Workspace {id: $workspaceId})
        RETURN c
        """)
    List<Concept> findAllByWorkspaceId(@Param("workspaceId") String workspaceId);

    /**
     * Returns all concepts in the public workspace (or all public concepts).
     * Used as the default graph when no workspace filter is applied.
     */
    @Query("""
        MATCH (c:Concept)-[:BELONGS_TO]->(w:Workspace)
        WHERE w.isPublic = true
        RETURN c
        """)
    List<Concept> findAllInPublicWorkspaces();

    // ─── Cluster / Tag filtering ──────────────────────────────────────────────

    /**
     * Returns concepts belonging to a specific tag (cluster).
     * Powers the "Vista de clúster y mapa curricular" feature.
     */
    @Query(
        value = """
            MATCH (c:Concept)-[:TAGGED_AS]->(t:Tag {id: $tagId})
            RETURN c
            """,
        countQuery = """
            MATCH (c:Concept)-[:TAGGED_AS]->(t:Tag {id: $tagId})
            RETURN count(c)
            """
    )
    Page<Concept> findByTagId(@Param("tagId") String tagId, Pageable pageable);

    @Query(
        value = """
            MATCH (c:Concept)-[:TAGGED_AS]->(t:Tag)
            WHERE toLower(t.name) = toLower($tagName)
            RETURN c
            """,
        countQuery = """
            MATCH (c:Concept)-[:TAGGED_AS]->(t:Tag)
            WHERE toLower(t.name) = toLower($tagName)
            RETURN count(c)
            """
    )
    Page<Concept> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    // ─── Prerequisites ────────────────────────────────────────────────────────

    @Query(
        "MATCH path = (start:Concept)-[:PREREQUISITE*]-(target:Concept) " +
            "WHERE target.id = $conceptId " +
            "WITH start, min(length(path)) AS pathLength " +
            "RETURN start " +
            "ORDER BY pathLength"
    )
    List<Concept> findAllPrerequisites(@Param("conceptId") String conceptId);

    @Query("RETURN EXISTS((:Concept {id: $startId})-[:PREREQUISITE*]-(:Concept {id: $endId}))")
    boolean existsPathBetween(@Param("startId") String startId, @Param("endId") String endId);

    // ─── Related concepts ─────────────────────────────────────────────────────

    /**
     * Returns concepts that have the given concept as a prerequisite (dependents).
     */
    @Query("""
        MATCH (dependent:Concept)-[:PREREQUISITE]->(c:Concept {id: $conceptId})
        RETURN dependent
        """)
    List<Concept> findDependents(@Param("conceptId") String conceptId);


    /**
     * Returns concepts that share at least one tag with the given concept,
     * excluding the concept itself. Used for "RELATED BRANCHES" in the detail panel.
     */
    @Query("""
        MATCH (c:Concept {id: $conceptId})-[:TAGGED_AS]->(t:Tag)<-[:TAGGED_AS]-(related:Concept)
        WHERE related.id <> $conceptId
        RETURN DISTINCT related
        LIMIT 10
        """)
    List<Concept> findRelatedConcepts(@Param("conceptId") String conceptId);

    /**
     * Returns the count of concepts directly connected (as prerequisite or dependent) to a concept.
     * Used for the "CONNECTIONS" count in the node detail panel.
     */
    @Query("""
        MATCH (c:Concept {id: $conceptId})
        OPTIONAL MATCH (c)-[:PREREQUISITE]-(neighbor:Concept)
        RETURN count(DISTINCT neighbor)
        """)
    int countConnections(@Param("conceptId") String conceptId);

    // ─── Flashcard → Concept lookup ──────────────────────────────────────────

    @Query("""
        MATCH (c:Concept)-[:HAS_FLASHCARD]->(f:Flashcard {id: $flashcardId})
        RETURN c
        """)
    Optional<Concept> findByFlashcardId(@Param("flashcardId") String flashcardId);

    // ─── Learning paths ───────────────────────────────────────────────────────

    /**
     * Topological sort of all concepts in a workspace by prerequisite depth.
     * Concepts with no prerequisites come first (depth=0), then their dependents.
     * Powers the "Rutas de aprendizaje guiadas" feature.
     */
    @Query("""
        MATCH (c:Concept)-[:BELONGS_TO]->(w:Workspace {id: $workspaceId})
        OPTIONAL MATCH path = (c)-[:PREREQUISITE*]->(leaf:Concept)
        WITH c, coalesce(max(length(path)), 0) AS depth
        ORDER BY depth ASC
        RETURN c
        """)
    List<Concept> findLearningPathByWorkspace(@Param("workspaceId") String workspaceId);
}
