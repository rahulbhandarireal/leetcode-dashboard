package com.example.LeetDeCode_Battle_Module.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmitRequest {
    @NotBlank private String language;
    @NotBlank(message = "code cannot be empty on submit") private String code;
}
