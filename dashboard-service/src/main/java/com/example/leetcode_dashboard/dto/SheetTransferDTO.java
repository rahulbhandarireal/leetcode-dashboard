package com.example.leetcode_dashboard.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SheetTransferDTO {
    private Long sheetId;

    private String name;

    private String description;
    private String type;

    private int total;
    private int easy;
    private int medium;
    private int hard;

}
