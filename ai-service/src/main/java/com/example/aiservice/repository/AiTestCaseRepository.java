package com.example.aiservice.repository;

import com.example.aiservice.model.AiTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiTestCaseRepository extends JpaRepository<AiTestCase,Long> {
}
