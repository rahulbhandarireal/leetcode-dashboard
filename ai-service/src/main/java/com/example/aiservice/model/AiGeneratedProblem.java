package com.example.aiservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGeneratedProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String topic;
    private String level;

    @Column(columnDefinition = "TEXT") // Safe storage for long competitive programming statements
    private String problemStatement;

    @Column(name = "problem_constraints") // Fix: 'constraint' is a reserved SQL keyword. This renames the DB column safely!
    private String constraint;

    @Column(columnDefinition = "TEXT")
    private String hints;

    // Joint column map to separate public sample cases from evaluation cases
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "problem_sample_id")
    private List<AiTestCase> sampleTestCases;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "problem_hidden_id")
    private List<AiTestCase> testcase;
}