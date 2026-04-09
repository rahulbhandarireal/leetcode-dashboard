package com.example.leetcode_dashboard.Service;


import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.Knows;
import com.example.leetcode_dashboard.model.Student;
import com.example.leetcode_dashboard.repository.KnowsRelationRepository;
import com.example.leetcode_dashboard.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RelationService {

    @Autowired
    private KnowsRelationRepository knowsRelationRepository;
    @Autowired
    private LeetCodeClient leetCodeClient;
    @Autowired
    private StudentRepository studentRepository;

    public boolean makeasfriend(String user, String friend) {
        try{
            UserStatsResponse b=leetCodeClient.getUserStats(friend);
        }catch(Exception e){
            return false;
        }
        UserStatsResponse a=leetCodeClient.getUserStats(user);

        Student knower = studentRepository.findByUsername(user);
        Student known = studentRepository.findByUsername(friend);

        Knows k = new Knows();
        k.setKnower(knower);
        k.setKnown(known);
        knowsRelationRepository.save(k);
        return true;

    }

    public List<UserStatsResponse> findallKnown(String user){
        List<Student> output=knowsRelationRepository.findAllKnownByUsername(user);
        List<UserStatsResponse> results=new ArrayList<>();
        output.forEach(x->{
           results.add(x.getUserStatsResponse());
        });
        return results;
    }


}
