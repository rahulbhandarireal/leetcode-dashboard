package com.example.LeetDeCode_Battle_Module.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Player-safe projection of ProblemStatement — this is what actually gets sent
 * over STOMP to the frontend. Deliberately excludes `testcase` (hidden set).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemView {
    private int id;
    private String title;
    private String topic;
    private String level;
    private String problemStatement;
    private String constraint;
    private String hints;
    private List<TestCaseDto> sampleTestCases;

    public static ProblemView from(ProblemStatement full) {
        return ProblemView.builder()
                .id(full.getId())
                .title(full.getTitle())
                .topic(full.getTopic())
                .level(full.getLevel())
                .problemStatement(full.getProblemStatement())
                .constraint(full.getConstraint())
                .hints(full.getHints())
                .sampleTestCases(full.getSampleTestCases())
                .build();
    }
}