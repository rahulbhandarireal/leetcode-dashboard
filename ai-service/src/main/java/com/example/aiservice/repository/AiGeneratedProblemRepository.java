package com.example.aiservice.repository;

import com.example.aiservice.model.AiGeneratedProblem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AiGeneratedProblemRepository extends JpaRepository<AiGeneratedProblem, Long> {

    @Query("SELECT COUNT(p.id) FROM AiGeneratedProblem p WHERE p.topic = :topic AND p.level = :level")
    int getProblemCount(@Param("topic") String topic, @Param("level") String level);

    AiGeneratedProblem getById(@Param("id") Long id);

    @Query(value = "SELECT * FROM ai_generated_problem WHERE topic = :topic AND level = :level ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<AiGeneratedProblem> findRandomByTopicAndLevel(@Param("topic") String topic, @Param("level") String level);
}
