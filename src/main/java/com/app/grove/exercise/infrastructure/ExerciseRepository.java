package com.app.grove.exercise.infrastructure;


import com.app.grove.exercise.domain.Exercise;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends Neo4jRepository<Exercise,String> {
    List<Exercise> findByType(String type);
    List<Exercise> findByDifficulty(Integer difficulty);
    List<Exercise> findByQuestionContaining(String keyword);

    @Query("MATCH(e:Exercise)-[:BELONGS_TO]->(u:User) WHERE u.id = $userId RETURN e")
    List<Exercise> findExerciseByUserId(@Param("userId") String userId);

}
