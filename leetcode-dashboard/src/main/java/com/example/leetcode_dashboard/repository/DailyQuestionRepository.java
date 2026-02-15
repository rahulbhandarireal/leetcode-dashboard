package com.example.leetcode_dashboard.repository;

import com.example.leetcode_dashboard.model.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyQuestionRepository extends JpaRepository<DailyQuestion,Integer> {
     DailyQuestion findByDate(String date);
}
