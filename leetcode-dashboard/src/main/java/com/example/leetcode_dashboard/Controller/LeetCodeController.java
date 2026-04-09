package com.example.leetcode_dashboard.Controller;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/leetcode")
public class LeetCodeController {
    private final LeetCodeClient leetCodeClient;

    public LeetCodeController(LeetCodeClient leetCodeClient) {
        this.leetCodeClient = leetCodeClient;
    }





    @GetMapping("/stats/{username}")
    public ApiResponse<UserStatsResponse> getUserStats(@PathVariable String username) {

        ApiResponse<UserStatsResponse> apiResponse=new ApiResponse<>();
        try {
            UserStatsResponse userStatsResponse = leetCodeClient.getUserStats(username);
            apiResponse.setData(userStatsResponse);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Success");
        }catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
        }

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


    
    @GetMapping("/ispotdsolved/{username}")
    public String isRecentSolvedProblem(@PathVariable String username){
        return leetCodeClient.isPOTDSolved(username);
    }







}
