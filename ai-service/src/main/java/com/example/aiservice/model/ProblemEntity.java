package com.example.aiservice.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


public record ProblemEntity(
        @JsonPropertyDescription("A creative, engaging title for the coding challenge.")
        String title,

        @JsonPropertyDescription("Detailed problem statement in clean Markdown format.")
        String problemStatement,

        @JsonPropertyDescription("Include constraints (e.g., N <= 10^5)")
        String constraint,
        @JsonPropertyDescription("Hints and optimal data structure")
        String hints,

        @JsonPropertyDescription("Exactly 2 sample test cases containing input, expected output, and a logical explanation.")
        List<TestCase> sampleTestCases
        ,
        @JsonPropertyDescription("Exactly 2 test cases with correct answer, no need of explanation")
        List<TestCase> testcase
) {}


