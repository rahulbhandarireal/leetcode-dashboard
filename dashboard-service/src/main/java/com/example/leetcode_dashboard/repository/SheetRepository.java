package com.example.leetcode_dashboard.repository;

import com.example.leetcode_dashboard.model.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SheetRepository extends JpaRepository<Sheet,Integer> {
    Optional<Sheet> findBySheetId(Long sheetId);

    List<Sheet> findAll();
}
