package com.example.leetcode_dashboard.schedular;


import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.component.DailyProblemHolder;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.repository.LeetCodeProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PotdScheduler {

    private final LeetCodeClient leetCodeClient;
    private final DailyProblemHolder dailyProblemHolder;

    // Runs every day at 6:00 AM
    @Scheduled(cron = "0 0 6 * * ?")
    public void fetchDailyProblem() {
        System.out.println("Fetching POTD...");
        QuestionTransferDTO questionTransferDTO=leetCodeClient.getProblemoftheDay();
        dailyProblemHolder.setCurrentProblem(questionTransferDTO);
        System.out.println("POTD fetched successfully");
    }
}
