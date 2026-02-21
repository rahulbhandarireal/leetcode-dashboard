package com.example.leetcode_dashboard.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientconfig {

    //Asynchronous and Non-blocking
    @Bean
    public WebClient leetCodeWebClient() {
        return WebClient.builder()
                .baseUrl("https://leetcode.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.REFERER, "https://leetcode.com")
                .defaultHeader(HttpHeaders.ORIGIN, "https://leetcode.com")
                .defaultHeader(HttpHeaders.USER_AGENT,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build();
    }

}

