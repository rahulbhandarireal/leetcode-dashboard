package com.example.leetcode_dashboard;

import com.example.leetcode_dashboard.Service.LeetCodeClient;
import com.example.leetcode_dashboard.component.DailyProblemHolder;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeetcodeDashboardApplication implements CommandLineRunner {


	@Autowired
	LeetCodeClient  leetCodeClient;
	@Autowired
	DailyProblemHolder dailyProblemHolder;


	static void main(String[] args) {
		SpringApplication.run(LeetcodeDashboardApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		QuestionTransferDTO questionTransferDTO=leetCodeClient.getProblemoftheDay();
		dailyProblemHolder.setCurrentProblem(questionTransferDTO);
	}
}
