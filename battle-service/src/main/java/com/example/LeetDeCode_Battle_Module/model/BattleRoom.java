package com.example.LeetDeCode_Battle_Module.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoom implements Serializable {

    private String roomCode;
    private String hostPlayerId;
    private int problemId;            // FK into problem:{id} cache
    private RoomStatus status;
    private String winnerPlayerId;

    @Builder.Default
    private List<PlayerState> players = new ArrayList<>();


    private LocalDateTime createdAt;

    public enum RoomStatus {
        WAITING_FOR_PLAYERS,
        IN_PROGRESS,
        COMPLETED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerState implements Serializable {
        private String playerId;
        private String username;
        private String currentCode;
        private int score;
        private boolean submitted;
        private String language;
        private java.time.Instant submittedAt;

    }
}