package com.example.leetcode_dashboard.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LeetCodeProblemClient {
    
    @Autowired
    private WebClient webClient;



    public LeetCodeProblemClient(WebClient webClient) {
        this.webClient = webClient;
    }

    





}
