package com.example.LeetDeCode_Battle_Module.controller;


import com.example.LeetDeCode_Battle_Module.DTO.ProblemStatement;
import com.example.LeetDeCode_Battle_Module.DTO.ProblemView;
import com.example.LeetDeCode_Battle_Module.exceptionhandler.ResourceNotFoundException;
import com.example.LeetDeCode_Battle_Module.response.ApiResponse;
import com.example.LeetDeCode_Battle_Module.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/getproblem")
public class ProblemController {



    @Autowired
    private ProblemService problemService;


    @GetMapping("/ai/")
    private ApiResponse<ProblemStatement> getProblem(@RequestParam int problemId) {

        ApiResponse<ProblemStatement> apiResponse = new ApiResponse<>();
        try {
            ProblemStatement problemStatement = problemService.getProblem(problemId);
            apiResponse.setData(problemStatement);
            apiResponse.setStatus(true);
            apiResponse.setMessage("success");
        } catch (Exception e) {
            apiResponse.setMessage(e.getMessage());
        }
          return apiResponse;
    }
    @GetMapping("/ai/bytopicandlevel")
    private ApiResponse<ProblemStatement> getprobelmbytopic(@RequestParam String topic,@RequestParam String level) {
        ApiResponse<ProblemStatement> apiResponse = new ApiResponse<>();
        try {
            ProblemStatement problemStatement = problemService.getRandomProblem(topic, level);
            apiResponse.setData(problemStatement);
            apiResponse.setStatus(true);
            apiResponse.setMessage("success");
        } catch (Exception e) {
            apiResponse.setStatus(false);
            apiResponse.setMessage(e.getMessage());
        }
        return apiResponse;
    }


}
