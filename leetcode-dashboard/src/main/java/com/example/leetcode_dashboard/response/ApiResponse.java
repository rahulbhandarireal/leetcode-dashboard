package com.example.leetcode_dashboard.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ApiResponse<T> {
    boolean success;
    T data;
    String message;
}
