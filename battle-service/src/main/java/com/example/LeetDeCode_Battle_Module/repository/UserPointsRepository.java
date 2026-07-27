package com.example.LeetDeCode_Battle_Module.repository;

import com.example.LeetDeCode_Battle_Module.model.Userpoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointsRepository extends JpaRepository<Userpoints,Integer> {

    Userpoints findByUsername(String username);

}
