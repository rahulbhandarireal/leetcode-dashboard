package com.example.LeetDeCode_Battle_Module.DTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // tolerate extra fields the AI service adds later
public class TestCaseDto implements Serializable {
    private Long id;
    private String input;
    private String output;
    private String explanation;
}
