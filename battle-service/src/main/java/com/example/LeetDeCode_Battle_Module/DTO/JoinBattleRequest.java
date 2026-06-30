package com.example.LeetDeCode_Battle_Module.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class JoinBattleRequest {

    @NonNull
    String player2;
    @NonNull
    String publicId;
}
