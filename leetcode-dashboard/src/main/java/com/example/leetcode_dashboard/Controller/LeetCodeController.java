package com.example.leetcode_dashboard.Controller;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.model.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/leetcode")
public class LeetCodeController {
    private final LeetCodeClient leetCodeClient;

    public LeetCodeController(LeetCodeClient leetCodeClient) {
        this.leetCodeClient = leetCodeClient;
    }

    @GetMapping("/stats/{username}")
    public Student getUserStats(@PathVariable String username) {
        return leetCodeClient.getUserStats(username);
    }



    @GetMapping("/questionoftheday")
        public QuestionTransferDTO getQuestionoftheday(){
        return leetCodeClient.getProblemoftheDay();
    }

    @GetMapping("/recentsolvedproblem/{username}")
    public List<LeetCodeProblem>  getRecentSolvedProblem(@PathVariable String username){
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
