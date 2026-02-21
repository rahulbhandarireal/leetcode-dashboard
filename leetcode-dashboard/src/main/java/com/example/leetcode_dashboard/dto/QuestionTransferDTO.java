package com.example.leetcode_dashboard.dto;

import com.example.leetcode_dashboard.model.LeetCodeProblem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionTransferDTO {
    private String date;

    private int problemId;

    private String title;
    private String titleSlug;
    private String difficulty;
    private String content;

    private int totalAcceptedRaw;
    private int totalSubmissionRaw;
    private String acceptanceRate;

    private List<String> hints;
    private List<String> topicTags;

    public LeetCodeProblem getSolvedProblem() {
        return LeetCodeProblem.builder()
                .problemId(problemId)
                .title(title)
                .content(content)
                .date(date)
                .difficulty(difficulty)
                .titleSlug(titleSlug)
                .hints(hints)
                .topicTags(topicTags)
                .acceptanceRate(acceptanceRate)
                .totalSubmissionRaw(totalSubmissionRaw)
                .totalAcceptedRaw(totalAcceptedRaw)
                .build();

    }

}
