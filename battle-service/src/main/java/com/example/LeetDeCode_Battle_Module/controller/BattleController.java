package com.example.LeetDeCode_Battle_Module.controller;


import com.example.LeetDeCode_Battle_Module.DTO.BattleRequest;
import com.example.LeetDeCode_Battle_Module.DTO.BattleResponse;
import com.example.LeetDeCode_Battle_Module.DTO.JoinBattleRequest;
import com.example.LeetDeCode_Battle_Module.model.Battle;
import com.example.LeetDeCode_Battle_Module.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/battle")
public class BattleController {


    @Autowired
    private BattleService battleService;

    @PostMapping("/create")
    public BattleResponse createBattle(@RequestBody  BattleRequest battle) {
        Battle battle1 = battleService.createBattle(battle);
        return BattleResponse.builder()
                .publicId(battle1.getPublicId())
                .questionFrontendId(battle1.getQuestionId())
                .build();
    }

    @PutMapping("/join")
    public BattleResponse joinBattle(@RequestBody JoinBattleRequest joinBattle) {
        Battle battle=battleService.joinBattle(joinBattle);
        return BattleResponse.builder()
                .publicId(battle.getPublicId())
                .questionFrontendId(battle.getQuestionId())
                .build();
    }




}
