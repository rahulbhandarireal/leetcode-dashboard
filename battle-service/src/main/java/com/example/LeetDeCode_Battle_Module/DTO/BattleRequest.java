package com.example.LeetDeCode_Battle_Module.DTO;


import com.example.LeetDeCode_Battle_Module.model.BattleState;
import com.example.LeetDeCode_Battle_Module.model.JoinState;
import com.example.LeetDeCode_Battle_Module.model.Topic;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BattleRequest {

    @NonNull
    String player1;

    @NonNull
    String player2;

    @Enumerated(EnumType.STRING)
    Topic topic;
}
