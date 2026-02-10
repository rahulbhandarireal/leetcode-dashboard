package com.example.leetcode_dashboard.dto;


public class UserStatsResponse {

    private String username;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;

    public UserStatsResponse(String username, int totalSolved, int easySolved, int mediumSolved, int hardSolved) {
        this.username = username;
        this.totalSolved = totalSolved;
        this.easySolved = easySolved;
        this.mediumSolved = mediumSolved;
        this.hardSolved = hardSolved;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalSolved() {
        return totalSolved;
    }

    public int getEasySolved() {
        return easySolved;
    }

    public int getMediumSolved() {
        return mediumSolved;
    }

    public int getHardSolved() {
        return hardSolved;
    }
}
