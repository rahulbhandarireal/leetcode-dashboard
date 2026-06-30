package com.example.leetcode_dashboard.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.parsing.Problem;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sheets")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private long sheetId;

    private String name;

    private String description;

    private int total;

    private int easy;

    private int medium;

    private int hard;

    private String type;


    @OneToMany(mappedBy = "sheet")
    private List<SheetProblem> sheetProblems =
            new ArrayList<>();


}