package com.example.LeetDeCode_Battle_Module.client;


import com.example.LeetDeCode_Battle_Module.DTO.ProblemStatement;
import com.example.LeetDeCode_Battle_Module.DTO.ProblemView;
import com.example.LeetDeCode_Battle_Module.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiProblemClient {

    private final RestClient restClient;

    public AiProblemClient(@Value("${ai-service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /** Fetch a specific problem by ID. */
    public ProblemStatement fetchById(int problemId) {
        return restClient.get()
                .uri("/get/problem/{id}", problemId)
                .retrieve()
                .body(ProblemStatement.class);
    }

    /** Fetch a random problem, optionally filtered — used when creating a new room. */
    public ProblemStatement fetchRandom(String topic, String level) {
        ParameterizedTypeReference<ApiResponse<ProblemStatement>> typeRef =
                new ParameterizedTypeReference<ApiResponse<ProblemStatement>>() {};

// 2. Pass it into the .body() method
        ApiResponse<ProblemStatement> result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/get/problem")
                        .queryParamIfPresent("topic", java.util.Optional.ofNullable(topic))
                        .queryParamIfPresent("level", java.util.Optional.ofNullable(level))
                        .build())
                .retrieve()
                .body(typeRef); // Jackson will now map both ApiResponse and ProblemStatement perfectly

        return result.getData();
    }
}