package com.example.LeetDeCode_Battle_Module.model;


import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private Integer frontendId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    private Integer usedCount = 0;
}