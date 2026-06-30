package com.example.LeetDeCode_Battle_Module.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResult {
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private boolean passed;
    private String error;       // stderr / exception message, if any
    private boolean timedOut;
}