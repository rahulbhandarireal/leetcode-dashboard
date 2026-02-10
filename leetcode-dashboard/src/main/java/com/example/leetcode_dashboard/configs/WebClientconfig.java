package com.example.leetcode_dashboard.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientconfig {

    //Asynchronous and Non-blocking
    @Bean
    public WebClient leetCodeWebClient() {
        return WebClient.builder()
                .baseUrl("https://leetcode.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

