package com.example.leetcode_dashboard.repository;

import com.example.leetcode_dashboard.model.SolvedProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem,Integer> {

    Optional<SolvedProblem> existsByStudent_UsernameAndProblem_ProblemId(String username, int problemId);

    @Query("select s from SolvedProblem s where s.student.username = ?1")
    List<SolvedProblem> findByUsername(String username);

}

