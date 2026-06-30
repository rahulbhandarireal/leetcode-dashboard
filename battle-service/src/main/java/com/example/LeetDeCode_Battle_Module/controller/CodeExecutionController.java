package com.example.LeetDeCode_Battle_Module.controller;

import com.example.LeetDeCode_Battle_Module.DTO.RunCodeRequest;
import com.example.LeetDeCode_Battle_Module.DTO.TestCaseResult;
import com.example.LeetDeCode_Battle_Module.service.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/execute")
public class CodeExecutionController {

    @Autowired
    private CodeExecutionService codeExecutionService;

    @PostMapping("/run")
    public List<TestCaseResult> runCode(@RequestBody RunCodeRequest request) {
        return codeExecutionService.runAllTestCases(request);
    }
}