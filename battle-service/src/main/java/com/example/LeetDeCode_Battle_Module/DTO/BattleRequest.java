package com.example.LeetDeCode_Battle_Module.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for POST /battle/create
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleRequest {

    private String hostPlayerId;

    @NotBlank(message = "hostUsername is required")
    private String hostUsername;

    // Optional — if blank/null, ProblemService.getRandomProblem() will fetch
    // a problem regardless of topic (see queryParamIfPresent in AiProblemClient)
    private String topic;

    // Optional — same as above, unconstrained level means "any difficulty"
    private String level;
}