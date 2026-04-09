package com.example.leetcode_dashboard;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.component.DailyProblemHolder;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeetcodeDashboardApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(LeetcodeDashboardApplication.class);

	@Autowired
	LeetCodeClient  leetCodeClient;
	@Autowired
	DailyProblemHolder dailyProblemHolder;


	 static void main(String[] args) {
		SpringApplication.run(LeetcodeDashboardApplication.class, args);
	}


	@Override
	public void run(String... args) {
		try {
			QuestionTransferDTO questionTransferDTO = leetCodeClient.getProblemoftheDay();
			dailyProblemHolder.setCurrentProblem(questionTransferDTO);
			log.info("Loaded daily problem during application startup");
		} catch (Exception e) {
			log.warn("Failed to load daily problem during startup. The scheduler can retry later.", e);
		}
	}
}
