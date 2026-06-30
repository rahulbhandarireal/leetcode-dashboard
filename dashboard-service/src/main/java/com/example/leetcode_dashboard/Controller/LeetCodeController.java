package com.example.leetcode_dashboard.Controller;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.dto.RatingDTO;
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
        public ApiResponse<QuestionTransferDTO> getQuestionoftheday(){
        QuestionTransferDTO questionTransferDTO= leetCodeClient.getProblemoftheDay();
        ApiResponse<QuestionTransferDTO> apiResponse=new ApiResponse<>();
        apiResponse.setData(questionTransferDTO);
        if(questionTransferDTO==null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Problem occurred");
        }else {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Success");
        }
        return apiResponse;
    }

    @GetMapping("/getquestion/{titleSlug}")
    public ApiResponse<QuestionTransferDTO> getQuestion(@PathVariable String titleSlug){
        QuestionTransferDTO questionTransferDTO= leetCodeClient.getQuestionByID(titleSlug);
        ApiResponse<QuestionTransferDTO> apiResponse=new ApiResponse<>();
        apiResponse.setData(questionTransferDTO);
        if(questionTransferDTO==null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Problem occurred");

        }else{
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Success");
        }
        return apiResponse;
    }

    @GetMapping("/recentsolvedproblem/{username}")
    public ApiResponse<List<QuestionTransferDTO>>  getRecentSolvedProblem(@PathVariable String username){
        username=normalizeUsername(username);
       List<QuestionTransferDTO> qto=leetCodeClient.getRecentSolvedProblems(username);
       ApiResponse<List<QuestionTransferDTO>> apiResponse=new ApiResponse<>();
       apiResponse.setData(qto);
       apiResponse.setSuccess(true);
       apiResponse.setMessage("Success");
       return apiResponse;
    }


    
    @GetMapping("/ispotdsolved/{username}")
    public ApiResponse<String> isRecentSolvedProblem(@PathVariable String username){
        String ans= leetCodeClient.isPOTDSolved(username);
        ApiResponse<String> apiResponse=new ApiResponse<>();
        apiResponse.setData(ans);
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Success");
        return apiResponse;
    }


    private String normalizeUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is required");
        }
        String normalizedUsername = username.trim();
        if (normalizedUsername.isEmpty()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        return normalizedUsername;
    }

    @GetMapping("/rating/{username}")
    private ApiResponse<List<RatingDTO>> getrating(@PathVariable  String username){
        List<RatingDTO> ratingDTOlist=leetCodeClient.getRating(username);
        ApiResponse<List<RatingDTO>> apiResponse=new ApiResponse<>();
        if (ratingDTOlist==null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Problem occurred");
        }else{
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Success");
            apiResponse.setData(ratingDTOlist);
        }
        return apiResponse;
    }








}
