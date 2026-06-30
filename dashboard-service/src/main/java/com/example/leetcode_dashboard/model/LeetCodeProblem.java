package com.example.leetcode_dashboard.model;

import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.configs.StringListJsonConverter;
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

    private int totalAcceptedRaw;
    private int totalSubmissionRaw;
    private String acceptanceRate;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> hints;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> topicTags;

    @OneToMany(mappedBy = "problem")
    private List<SheetProblem> sheetProblems =
            new ArrayList<>();

//    removing the bidirectional mapping
//    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL,
//            orphanRemoval = true,fetch = FetchType.LAZY)
//    private List<SolvedProblem> solvedProblems = new ArrayList<>();

    public QuestionTransferDTO getQuestion(){
        return  QuestionTransferDTO.builder()
                .problemId(problemId)
                .title(title)
                .difficulty(difficulty)
                .titleSlug(titleSlug)
                .totalAcceptedRaw(totalAcceptedRaw)
                .totalSubmissionRaw(totalSubmissionRaw)
                .acceptanceRate(acceptanceRate)
                .hints(hints)
                .topicTags(topicTags)
                .build();
    }

}
