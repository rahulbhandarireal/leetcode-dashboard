package com.example.aiservice.dto;


import com.example.aiservice.model.AiGeneratedProblem;
import com.example.aiservice.model.AiTestCase;
import com.example.aiservice.model.TestCase;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class AIProblemDTO {
    private int id;


    String title;

    String topic;
    String level;


    String problemStatement;


    String constraint;

    String hints;


    List<AiTestCase> sampleTestCases;

    List<AiTestCase> testcase;

    public AiGeneratedProblem getAiGeneratedProblem() {
        return AiGeneratedProblem.builder()
                .problemStatement(problemStatement)
                .hints(hints)
                .constraint(constraint)
                .sampleTestCases(sampleTestCases)
                .testcase(testcase)
                .level(level)
                .topic(topic)
                .title(title)
                .build();
    }

    public static AIProblemDTO convertToAIDto(AiGeneratedProblem existingProblem) {
        return AIProblemDTO.builder()
        .id(existingProblem.getId())
        .title(existingProblem.getTitle())
        .topic(existingProblem.getTopic())
        .level(existingProblem.getLevel())
        .problemStatement(existingProblem.getProblemStatement())
        .constraint(existingProblem.getConstraint())
        .hints(existingProblem.getHints())
        .sampleTestCases(existingProblem.getSampleTestCases())
        .testcase(existingProblem.getTestcase())
        .build();
    }


}
