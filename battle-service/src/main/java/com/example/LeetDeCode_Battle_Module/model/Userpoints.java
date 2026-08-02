package com.example.LeetDeCode_Battle_Module.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Userpoints {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    @Column(nullable = false,unique = true)
    private String username;

    private int decodePoints;

    @JsonIgnoreProperties
    public Userpoints() {
        decodePoints = 0;
    }

}
