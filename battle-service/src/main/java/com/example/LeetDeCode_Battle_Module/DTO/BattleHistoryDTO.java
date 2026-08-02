package com.example.LeetDeCode_Battle_Module.DTO;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleHistoryDTO {
    private String playerB;
    private boolean win;
    private LocalDateTime startTime;
}
