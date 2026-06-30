package com.example.leetcode_dashboard.schedular;


import com.example.leetcode_dashboard.LeetcodeDashboardApplication;
import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.component.DailyProblemHolder;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.repository.LeetCodeProblemRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PotdScheduler {

    private final LeetCodeClient leetCodeClient;
    private final DailyProblemHolder dailyProblemHolder;

    private static final Logger log = LoggerFactory.getLogger(PotdScheduler.class);


    // Runs every day at 6:00 AM
    @Scheduled(cron = "0 0 6 * * ?")
    public void fetchDailyProblem() {
        System.out.println("Fetching POTD...");
        try {
            QuestionTransferDTO questionTransferDTO = leetCodeClient.getProblemoftheDay();
            dailyProblemHolder.setCurrentProblem(questionTransferDTO);
            log.info("Fetched POTD Successfully");
        }catch (Exception e){
            log.warn("POTD Schedular failed to fetched, will retry again {}", e.getMessage());
        }
    }
}
