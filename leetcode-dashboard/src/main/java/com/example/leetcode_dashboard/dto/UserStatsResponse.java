package com.example.leetcode_dashboard.dto;


import com.example.leetcode_dashboard.model.Student;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponse {

    private String username;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private int totalActiveDays;
    private String rating;
    private int streak;




}
