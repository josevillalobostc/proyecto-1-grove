package com.app.grove.tag.infrastructure;

import com.app.grove.tag.domain.Tag;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends Neo4jRepository<Tag, String> {
    Tag findByName(String name);

    Optional<Tag> findById(String id);

    @Query(
        value = "MATCH (t:Tag) WHERE toLower(t.name) CONTAINS toLower($keyword) RETURN t",
        countQuery = "MATCH (t:Tag) WHERE toLower(t.name) CONTAINS toLower($keyword) RETURN count(t)"
    )
    Page<Tag> searchByName(@Param("keyword") String keyword, Pageable pageable);
}
