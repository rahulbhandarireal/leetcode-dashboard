package com.example.leetcode_dashboard.Service;


import com.example.leetcode_dashboard.configs.WebClientconfig;
import com.example.leetcode_dashboard.dto.GraphQLRequest;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;


@Service
public class LeetCodeClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LeetCodeClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public UserStatsResponse getUserStats(String username){
        String query = """
        query userProfile($username: String!) {
          matchedUser(username: $username) {
            username
            submitStats {
              acSubmissionNum {
                difficulty
                count
              }
            }
          }
        }
        """;
        GraphQLRequest request =
                new GraphQLRequest(query, Map.of("username",username));
        String rawJson=  webClient.post()
                .uri("/graphql")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            JsonNode userNode = root.path("data").path("matchedUser");
            if(userNode.isMissingNode()|| userNode == null) throw new RuntimeException("User not found on LeetCode");
            String user = userNode.path("username").asText();

            JsonNode statsArray = userNode.path("submitStats").path("acSubmissionNum");

            int total = 0, easy = 0, medium = 0, hard = 0;

            for (JsonNode stat : statsArray) {
                String difficulty = stat.path("difficulty").asText();
                int count = stat.path("count").asInt();

                switch (difficulty) {
                    case "All" -> total = count;
                    case "Easy" -> easy = count;
                    case "Medium" -> medium = count;
                    case "Hard" -> hard = count;
                }
            }

            return new UserStatsResponse(user, total, easy, medium, hard);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LeetCode response", e);
        }
    }

}
