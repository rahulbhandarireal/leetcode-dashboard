package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.DTO.BattleRequest;
import com.example.LeetDeCode_Battle_Module.DTO.JoinBattleRequest;
import com.example.LeetDeCode_Battle_Module.exceptionhandler.BattelNotJoined;
import com.example.LeetDeCode_Battle_Module.exceptionhandler.BattleNotCreated;
import com.example.LeetDeCode_Battle_Module.model.*;
import com.example.LeetDeCode_Battle_Module.repository.BattleRepository;
import com.example.LeetDeCode_Battle_Module.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;



@Service
public class BattleServiceImpl implements BattleService {

    @Autowired
   private BattleRepository battleRepository;
    @Autowired
   private QuestionRepository questionRepository;


    @Override
    @Transactional
    public Battle createBattle(BattleRequest battleRequest) {
        Battle result=new Battle();
        result.setPlayer1(battleRequest.getPlayer1());
        result.setPlayer2(battleRequest.getPlayer2());
        Battle output=null;
        Topic topic=battleRequest.getTopic();
        try {
            result.setBattleState(BattleState.Pending);
            result.setJoinState(JoinState.NotJoined);
            Question question=questionRepository.findLeastUsedQuestion(topic, PageRequest.of(0,1)).get(0);
            result.setQuestionId(question.getFrontendId());
            question.setUsedCount(question.getUsedCount()+1);
             output = battleRepository.save(result);
        }catch (BattleNotCreated battleNotCreated){
            throw  new BattleNotCreated("Failed to create Battle");
        }
        return output;
    }

    @Override
    @Transactional
    public Battle joinBattle(JoinBattleRequest joinBattleRequest) {
        Battle battle;
        try {

             battle = battleRepository.
                    findByPublicId(joinBattleRequest.getPublicId());
             if(!battle.getPlayer2().equals(joinBattleRequest.getPlayer2())){
                 throw new BattelNotJoined("Not Allowed to join this battle");
             }
             if(battle.getBattleState()==BattleState.Declared){
                 throw new BattelNotJoined("Battle has already been declared");
             }else {
                 battle.setBattleState(BattleState.Pending);
             }
             if(battle.getJoinState()==JoinState.Joined){
              throw new BattelNotJoined("Battle has already been joined");
             }else {
                 battle.setJoinState(JoinState.Joined);
             }
             } catch (BattelNotJoined e) {
            throw e;
        }
        return battle;
    }
}
