package com.example.leetcode_dashboard.model;

import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeetCodeProblem {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true,nullable = false)
    private int problemId;

    private String title;
    @Column(unique = true, nullable = false)
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
//    removing the bidirectional mapping
//    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL,
//            orphanRemoval = true,fetch = FetchType.LAZY)
//    private List<SolvedProblem> solvedProblems = new ArrayList<>();

    public QuestionTransferDTO getQuestion(){
        return  QuestionTransferDTO.builder()
                .problemId(problemId)
                .title(title)
                .difficulty(difficulty)
                .content(content)
                .titleSlug(titleSlug)
                .totalAcceptedRaw(totalAcceptedRaw)
                .totalSubmissionRaw(totalSubmissionRaw)
                .acceptanceRate(acceptanceRate)
                .hints(hints)
                .topicTags(topicTags)
                .build();
    }

}
