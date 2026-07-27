package com.example.LeetDeCode_Battle_Module.repository;

import com.example.LeetDeCode_Battle_Module.model.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface BattleRepository extends JpaRepository<Battle, UUID> {

    Battle findByRoomCode(String roomCode);

    @Query("""
            SELECT b
            FROM Battle b
            WHERE b.playerA.username = :username
               OR b.playerB.username = :username
            ORDER BY b.createdAt DESC
            """)
    List<Battle> findBattleHistoryByUsername(@Param("username") String username);
}
