package com.example.leetcode_dashboard.dto;


import com.example.leetcode_dashboard.model.Student;

public class UserStatsResponse {

    private String username;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private int totalSubmit;
    private String acceptanceRate;


    public UserStatsResponse(String username, int totalSolved, int easySolved, int mediumSolved, int hardSolved, int totalSubmit) {
        this.username = username;
        this.totalSolved = totalSolved;
        this.easySolved = easySolved;
        this.mediumSolved = mediumSolved;
        this.hardSolved = hardSolved;
        this.totalSubmit = totalSubmit;
        double temp=((double) totalSolved /totalSubmit)*100;
        acceptanceRate=String.format("%.2f",temp);
    }

    public String getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(String acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
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

    public Student getStudent() {
        Student student= new Student();
        student.setEasy(easySolved);
        student.setHard(hardSolved);
        student.setMedium(mediumSolved);
        student.setUsername(username);
        student.setTotalSolved(totalSolved);
        student.setAcceptanceRate(acceptanceRate);
        return student;
    }
}
