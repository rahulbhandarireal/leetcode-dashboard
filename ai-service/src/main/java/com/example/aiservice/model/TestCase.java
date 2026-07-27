package com.example.aiservice.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record TestCase(
        @JsonPropertyDescription("The input value(s) exactly as they would be provided via standard input (e.g., '5\\n1 2 3 4 5').")
        String input,

        @JsonPropertyDescription("The expected output string.")
        String output,

        @JsonPropertyDescription("Step-by-step breakdown explaining why this input maps to this specific output.")
        String explanation
) {}
