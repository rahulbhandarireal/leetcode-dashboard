package com.example.leetcode_dashboard.repository;

import com.example.leetcode_dashboard.model.LeetCodeProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeetCodeProblemRepository extends JpaRepository<LeetCodeProblem,Integer> {

    LeetCodeProblem findByProblemId(int problemID);
    @Query("SELECT p FROM LeetCodeProblem p WHERE p.titleSlug = :titleSlug")
    LeetCodeProblem findProblemByTitleSlug(String titleSlug);

}
