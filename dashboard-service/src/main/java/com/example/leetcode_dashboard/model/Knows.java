package com.example.leetcode_dashboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"knower_id", "known_id"})
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Knows {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "knower_id", nullable = false)
    private Student knower;

    // B (who is known)
    @ManyToOne
    @JoinColumn(name = "known_id", nullable = false)
    private Student known;
}
