package com.example.leetcode_dashboard.Controller;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.DailyQuestion;
import com.example.leetcode_dashboard.model.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public DailyQuestion getQuestionoftheday(){
        return leetCodeClient.getProblemoftheDay();
    }

}
