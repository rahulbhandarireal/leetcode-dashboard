package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.DTO.BattleRequest;
import com.example.LeetDeCode_Battle_Module.DTO.JoinBattleRequest;
import com.example.LeetDeCode_Battle_Module.model.Battle;

public interface BattleService {


    Battle createBattle(BattleRequest battle);


    Battle joinBattle(JoinBattleRequest joinBattleRequest);
}
