package com.example.leetcode_dashboard.Service;


import com.example.leetcode_dashboard.CustomException.NotFoundException;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.Knows;
import com.example.leetcode_dashboard.model.Student;
import com.example.leetcode_dashboard.repository.KnowsRelationRepository;
import com.example.leetcode_dashboard.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public UserStatsResponse makeasfriend(String user, String friend) {
        UserStatsResponse a,b;
        try{
             b=leetCodeClient.getUserStats(friend);
        }catch(Exception e){
             throw new NotFoundException("No user found as username "+friend);
        }
        try{
            a=leetCodeClient.getUserStats(user);
        }catch(Exception e){
            throw new NotFoundException("No user found as username "+user);
        }


        Student knower = studentRepository.findByUsername(user);
        Student known = studentRepository.findByUsername(friend);

        Knows k = new Knows();
        k.setKnower(knower);
        k.setKnown(known);
        knowsRelationRepository.save(k);
        return b;
    }

    public List<UserStatsResponse> findallKnown(String user){
        List<Student> output=knowsRelationRepository.findAllKnownByUsername(user);
        List<UserStatsResponse> results=new ArrayList<>();
        output.forEach(x->{
            if (x != null &&
                    x.getUpdatedAt() != null &&
                    x.getUpdatedAt().isBefore(LocalDateTime.now().minusMinutes(30))) {
                UserStatsResponse student=leetCodeClient.getUserStats(x.getUsername());
                results.add(student);
            }else {
                results.add(x.getUserStatsResponse());
            }
        });
        return results;
    }

    @Transactional
    public boolean deleteKnown(String user, String known) {
        return knowsRelationRepository.deleteByKnowerUsernameAndKnownUsername(user, known) > 0;
    }


}
