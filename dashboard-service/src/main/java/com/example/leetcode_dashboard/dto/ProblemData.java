package com.example.leetcode_dashboard.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemData {

    private Integer problemId;

    private String title;

    private String titleSlug;

    private String difficulty;

    private String topic;

    private Integer position;
}