package com.example.leetcode_dashboard.Controller;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.model.Student;
import com.example.leetcode_dashboard.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/leetcode")
public class LeetCodeController {
    private final LeetCodeClient leetCodeClient;

    public LeetCodeController(LeetCodeClient leetCodeClient) {
        this.leetCodeClient = leetCodeClient;
    }


    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Backend Working");
    }

    @GetMapping("/stats/{username}")
    public ApiResponse<UserStatsResponse> getUserStats(@PathVariable String username) {
        UserStatsResponse userStatsResponse=leetCodeClient.getUserStats(username);
        ApiResponse<UserStatsResponse> apiResponse=new ApiResponse<>();
        apiResponse.setData(userStatsResponse);
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Success");
        return apiResponse;
    }



    @GetMapping("/questionoftheday")
        public QuestionTransferDTO getQuestionoftheday(){
        return leetCodeClient.getProblemoftheDay();
    }

    @GetMapping("/recentsolvedproblem/{username}")
    public List<QuestionTransferDTO>  getRecentSolvedProblem(@PathVariable String username){
        return leetCodeClient.getRecentSolvedProblems(username);
    }

    @GetMapping("/getranking/{username}")
    public String getrank(@PathVariable String username){
        return STR."\{leetCodeClient.getRanking(username)}";
    }
    
    @GetMapping("/ispotdsolved/{username}")
    public String isRecentSolvedProblem(@PathVariable String username){
        return leetCodeClient.isPOTDSolved(username);
    }







}
