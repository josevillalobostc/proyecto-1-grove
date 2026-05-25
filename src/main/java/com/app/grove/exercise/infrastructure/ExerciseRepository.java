package com.app.grove.exercise.infrastructure;

import com.app.grove.exercise.domain.Exercise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends Neo4jRepository<Exercise, String> {
    Page<Exercise> findByType(String type, Pageable pageable);
    Page<Exercise> findByDifficulty(Integer difficulty, Pageable pageable);
    Page<Exercise> findByQuestionContaining(String keyword, Pageable pageable);

    @Query(
        value = "MATCH(e:Exercise)-[:BELONGS_TO]->(u:User) WHERE u.id = $userId RETURN e",
        countQuery = "MATCH(e:Exercise)-[:BELONGS_TO]->(u:User) WHERE u.id = $userId RETURN count(e)"
    )
    Page<Exercise> findExerciseByUserId(@Param("userId") String userId, Pageable pageable);
}
