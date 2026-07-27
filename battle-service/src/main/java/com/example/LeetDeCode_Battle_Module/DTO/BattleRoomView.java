package com.example.LeetDeCode_Battle_Module.DTO;

import com.example.LeetDeCode_Battle_Module.model.BattleRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * What actually goes out over STOMP to /topic/room/{roomCode}.
 * Deliberately excludes PlayerState.currentCode — opponents get a progress
 * signal only, never the raw code, to keep the "battle" fair.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoomView {

    private String roomCode;
    private int problemId;
    private BattleRoom.RoomStatus status;
    private LocalDateTime startedAt;
    private List<PlayerProgressView> players;
    private String winnerPlayerId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerProgressView {
        private String playerId;
        private String username;
        private int score;
        private boolean submitted;
        private int lineCount;   // proxy for "progress" — cheap, no code leaked
    }

    public static BattleRoomView from(BattleRoom room) {
        return BattleRoomView.builder()
                .roomCode(room.getRoomCode())
                .winnerPlayerId(room.getWinnerPlayerId())
                .problemId(room.getProblemId())
                .status(room.getStatus())
                .startedAt(room.getCreatedAt())
                .players(room.getPlayers().stream()
                        .map(BattleRoomView::toProgressView)
                        .collect(Collectors.toList()))
                .build();
    }

    private static PlayerProgressView toProgressView(BattleRoom.PlayerState p) {
        String code = p.getCurrentCode();
        int lineCount = (code == null || code.isBlank()) ? 0 : (int) code.lines().count();

        return PlayerProgressView.builder()
                .playerId(p.getPlayerId())
                .username(p.getUsername())
                .score(p.getScore())
                .submitted(p.isSubmitted())
                .lineCount(lineCount)
                .build();
    }
}
