package com.example.leetcode_dashboard.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class DailyQuestion {

    @Column(unique = true)
    private String date;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private int problemId;
    private String title;
    private String titleSlug;
    private String difficulty;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private int totalAcceptedRaw;
    private int totalSubmissionRaw;
    private String acceptanceRate;

    private List<String> hints;
    private List<String> topicTags;

    public DailyQuestion() {
    }

    public DailyQuestion(String date,
                         int problemId,
                         String title,
                         String titleSlug,
                         String difficulty,
                         String content,
                         int totalAcceptedRaw,
                         int totalSubmissionRaw,
                         String acceptanceRate,
                         List<String> hints,
                         List<String> topicTags) {
        this.date = date;
        this.problemId = problemId;
        this.title = title;
        this.titleSlug = titleSlug;
        this.difficulty = difficulty;
        this.content = content;
        this.totalAcceptedRaw = totalAcceptedRaw;
        this.totalSubmissionRaw = totalSubmissionRaw;
        this.acceptanceRate = acceptanceRate;
        this.hints = hints;
        this.topicTags = topicTags;
    }

    public String getDate() {
        return date;
    }

    public int getProblemId() {
        return problemId;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getContent() {
        return content;
    }

    public int getTotalAcceptedRaw() {
        return totalAcceptedRaw;
    }

    public int getTotalSubmissionRaw() {
        return totalSubmissionRaw;
    }

    public String getAcceptanceRate() {
        return acceptanceRate;
    }

    public List<String> getHints() {
        return hints;
    }

    public List<String> getTopicTags() {
        return topicTags;
    }
}
