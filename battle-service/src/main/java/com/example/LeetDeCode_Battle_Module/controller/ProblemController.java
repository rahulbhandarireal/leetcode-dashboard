package com.example.LeetDeCode_Battle_Module.controller;


import com.example.LeetDeCode_Battle_Module.DTO.ProblemStatement;
import com.example.LeetDeCode_Battle_Module.DTO.ProblemView;
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
          ProblemStatement problemStatement= problemService.getProblem(problemId);
          ApiResponse<ProblemStatement> apiResponse = new ApiResponse<>();
          apiResponse.setData(problemStatement);
          apiResponse.setStatus(true);
          apiResponse.setMessage("success");
          return apiResponse;
    }
    @GetMapping("/ai/bytopicandlevel")
    private ApiResponse<ProblemStatement> getprobelmbytopic(@RequestParam String topic,@RequestParam String level) {
        ApiResponse<ProblemStatement> apiResponse = new ApiResponse<>();
        ProblemStatement problemStatement=problemService.getRandomProblem(topic,level);
        apiResponse.setData(problemStatement);
        apiResponse.setStatus(true);
        apiResponse.setMessage("success");
        return apiResponse;
    }


}
