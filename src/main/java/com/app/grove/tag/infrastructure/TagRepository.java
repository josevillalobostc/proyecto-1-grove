package com.app.grove.tag.infrastructure;

import com.app.grove.tag.domain.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends Neo4jRepository<Tag, String> {
    Tag findByName(String name);

    Optional<Tag> findById(String id);

    @Query(
        "MATCH (t:Tag) WHERE toLower(t.name) CONTAINS toLower($keyword) RETURN t"
    )
    List<Tag> searchByName(@Param("keyword") String keyword);
}
