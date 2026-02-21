package com.example.leetcode_dashboard.schedular;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.component.DailyProblemHolder;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.repository.LeetCodeProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class DailyPOTD {
    private final DailyProblemHolder holder;

    @Autowired
    private final LeetCodeClient client;

    @Autowired
    LeetCodeProblemRepository leetCodeProblemRepository;

    @Scheduled(cron = "0 31 5 * * ?")
    public void updateDailyValue() {
        System.out.println("Running daily update at 5:31 AM");
        LeetCodeProblem leetCodeProblem = client.getProblemoftheDay_Schedular();
       holder.setLeetCodeProblem(leetCodeProblem);
    }
}
