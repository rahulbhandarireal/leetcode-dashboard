package com.example.LeetDeCode_Battle_Module.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunCodeRequest {
    private String language;
    private String code;
    private List<TestCaseInput> testCases;
}