package com.example.LeetDeCode_Battle_Module.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeUpdateRequest {
    private String code; // blank allowed (cleared editor)
}