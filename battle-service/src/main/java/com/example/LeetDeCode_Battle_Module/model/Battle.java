package com.example.LeetDeCode_Battle_Module.model;


import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Battle {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String publicId;

    @NonNull
    @Column(nullable = false)
    String player1;

    @NonNull
    @Column(nullable = false)
    String player2;

    Integer  questionId;

    @Enumerated(EnumType.STRING)
    Topic  topic;

    @Enumerated(EnumType.STRING)
    BattleState battleState;

    @Enumerated(EnumType.STRING)
    JoinState  joinState;


    String winner;

    @PrePersist
    public void  generatePublicId(){
        if(publicId == null) {
            publicId = UUID.randomUUID().toString();
        }
    }





}
