package com.example.LeetDeCode_Battle_Module.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for PUT /battle/join
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinBattleRequest {

    @NotBlank(message = "roomCode is required")
    private String roomCode;

    @NotBlank(message = "playerId is required")
    private String playerId;

    @NotBlank(message = "username is required")
    private String username;
}