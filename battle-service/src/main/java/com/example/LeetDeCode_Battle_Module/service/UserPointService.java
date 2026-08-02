package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.DTO.BattleHistoryDTO;
import com.example.LeetDeCode_Battle_Module.exceptionhandler.ResourceNotFoundException;
import com.example.LeetDeCode_Battle_Module.model.Battle;
import com.example.LeetDeCode_Battle_Module.model.Userpoints;
import com.example.LeetDeCode_Battle_Module.repository.BattleRepository;
import com.example.LeetDeCode_Battle_Module.repository.UserPointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserPointService {

    @Autowired
    private UserPointsRepository userPointsRepository;

    @Autowired
    private BattleRepository battleRepository;

     public boolean addUser(String username){
        if(userPointsRepository.findByUsername(username) != null) return false;
        Userpoints userpoints = new Userpoints();
        userpoints.setUsername(username);
        userPointsRepository.save(userpoints);
        return true;
    }
    public Userpoints getUserpoint(String username){
         Userpoints userpoints = userPointsRepository.findByUsername(username);
         if(userpoints==null){
             userpoints = new Userpoints();
             userpoints.setUsername(username);
             userpoints=userPointsRepository.save(userpoints);
         }
         return userpoints;
    }
    public Userpoints updateUserPoints(String username, int score){
         Userpoints userpoints = userPointsRepository.findByUsername(username);
         if(userpoints==null){throw  new ResourceNotFoundException("No user points found for username: "+username);
         }
         userpoints.setDecodePoints(score+userpoints.getDecodePoints());
         userPointsRepository.save(userpoints);
         return userpoints;
    }

    public List<BattleHistoryDTO> getHistory(String username){
         List<Battle> battleList=battleRepository.findBattleHistoryByUsername(username);
        List<BattleHistoryDTO>  battleHistoryDTOList=new ArrayList<>();

         battleList.forEach(battle->{
            BattleHistoryDTO b= BattleHistoryDTO.builder()
                     .win(Objects.equals(username, battle.getWinnerId()))
                     .startTime(battle.getCreatedAt())
                     .playerB(Objects.equals(battle.getPlayerA().getUsername(), username)
                     ? battle.getPlayerB().getUsername():battle.getPlayerA().getUsername())
                     .build();
             battleHistoryDTOList.add(b);
         });
         return battleHistoryDTOList;
    }
}
