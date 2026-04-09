package com.example.leetcode_dashboard.repository;

import com.example.leetcode_dashboard.model.Knows;
import com.example.leetcode_dashboard.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowsRelationRepository extends JpaRepository<Knows,Long> {

    @Query("SELECT k.known FROM Knows k WHERE k.knower.username = :username")
    List<Student> findAllKnownByUsername(String username);


}
