package com.example.LeetDeCode_Battle_Module.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * This is the FULL problem as received from the AI service and stored in Redis
 * (problem:{id}). It INCLUDES hidden test cases — this object must NEVER be
 * serialized directly into a WebSocket broadcast. Use ProblemView (below) for
 * anything sent to the frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemStatement implements Serializable {
    private int id;
    private String title;
    private String topic;
    private String level;              // matches AiGeneratedProblem.level (Easy/Medium/Hard style)
    private String problemStatement;
    private String constraint;         // note: singular, matches upstream field name exactly
    private String hints;
    private List<TestCaseDto> sampleTestCases;  // safe to show players
    private List<TestCaseDto> testcase;         // HIDDEN — grading only, never broadcast
}
