package com.example.leetcode_dashboard.model;

import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true,nullable = false)
    private String username;

    private int totalSolved;

    private int hard;
    private int easy;
    private int medium;
    private String rating;
    private int streak;
    private int totalActiveDays;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SolvedProblem> solvedProblems = new ArrayList<>();


    @JsonIgnore
    public UserStatsResponse getUserStats(){
        return UserStatsResponse.builder()
                .username(username)
                .totalSolved(totalSolved)
                .easySolved(easy)
                .hardSolved(hard)
                .streak(streak)
                .totalActiveDays(totalActiveDays)
                .mediumSolved(medium)
                .rating(rating)
                .build();

    }


}
