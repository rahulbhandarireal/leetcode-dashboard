package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.DTO.RunCodeRequest;
import com.example.LeetDeCode_Battle_Module.DTO.TestCaseInput;
import com.example.LeetDeCode_Battle_Module.DTO.TestCaseResult;
import com.example.LeetDeCode_Battle_Module.enums.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeExecutionService {

    @Autowired
    private DockerRunnerService dockerRunnerService;

    public List<TestCaseResult> runAllTestCases(RunCodeRequest request) {
        List<TestCaseResult> results = new ArrayList<>();

        for (TestCaseInput testCase : request.getTestCases()) {
            ExecutionResult execResult = dockerRunnerService.run(
                    Language.fromString(request.getLanguage()),
                    request.getCode(),
                    testCase.getInput()
            );

            TestCaseResult result = buildResult(testCase, execResult);
            results.add(result);
        }

        return results;
    }

    private TestCaseResult buildResult(TestCaseInput testCase, ExecutionResult execResult) {
        if (execResult.isTimedOut()) {
            return TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getExpectedOutput())
                    .actualOutput("")
                    .passed(false)
                    .timedOut(true)
                    .error("Time Limit Exceeded")
                    .build();
        }

        if (execResult.getStderr() != null && !execResult.getStderr().isEmpty()) {
            return TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getExpectedOutput())
                    .actualOutput("")
                    .passed(false)
                    .timedOut(false)
                    .error(execResult.getStderr())
                    .build();
        }

        boolean passed = execResult.getStdout().trim()
                .equals(testCase.getExpectedOutput().trim());

        return TestCaseResult.builder()
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .actualOutput(execResult.getStdout())
                .passed(passed)
                .timedOut(false)
                .build();
    }
}