package com.example.leetcode_dashboard.repository;

import com.example.leetcode_dashboard.model.Sheet;
import com.example.leetcode_dashboard.model.SheetProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SheetProblemRepository extends JpaRepository<SheetProblem, Integer> {
    List<SheetProblem> findBySheet(Sheet sheet);
}
