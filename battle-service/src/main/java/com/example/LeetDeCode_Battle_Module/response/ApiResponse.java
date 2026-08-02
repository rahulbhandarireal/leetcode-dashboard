package com.example.LeetDeCode_Battle_Module.response;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T>{
        private boolean status;
        private String message;
        private T data;
}
