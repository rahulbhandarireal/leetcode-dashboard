package com.example.LeetDeCode_Battle_Module.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class submitCodeDTO<T> {
    private T data;
    private boolean allPassed;
    private int failedCase;
    private String errorMessage;
    private boolean isWinner;
}
