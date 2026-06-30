package com.example.leetcode_dashboard.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDTO {
           private int problemsSolved;
           private String rating;
}
