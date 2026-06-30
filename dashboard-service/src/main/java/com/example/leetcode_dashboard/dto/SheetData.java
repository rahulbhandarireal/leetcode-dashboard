package com.example.leetcode_dashboard.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SheetData {

    private Long sheetId;

    private String name;

    private String description;
    private String type;

    private List<ProblemData> problems;
}
