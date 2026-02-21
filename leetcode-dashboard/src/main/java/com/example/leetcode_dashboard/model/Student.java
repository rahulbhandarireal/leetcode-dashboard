package com.example.leetcode_dashboard.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String username;

    private int totalSolved;

    private int hard;
    private int easy;
    private int medium;
    private String rating;
    private int streak;
    private int totalActiveDays;


}
