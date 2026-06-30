package com.example.leetcode_dashboard.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sheet_problems")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SheetProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sheet_id",referencedColumnName = "sheetId")
    private Sheet sheet;

    @ManyToOne
    @JoinColumn(name = "problem_id",referencedColumnName = "problemId")
    private LeetCodeProblem problem;

    private Integer position;
}
