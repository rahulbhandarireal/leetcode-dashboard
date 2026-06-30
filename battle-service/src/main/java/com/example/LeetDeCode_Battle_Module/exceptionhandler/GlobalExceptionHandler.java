package com.example.LeetDeCode_Battle_Module.exceptionhandler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler  {


    @ExceptionHandler(BattleNotCreated.class)
    public ResponseEntity<?> handleBattleNotCreated(BattleNotCreated battleNotCreated) {
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("error",battleNotCreated.getMessage()));
    }

    @ExceptionHandler(BattelNotJoined.class)
    public ResponseEntity<?> handleBattelNotJoined(BattelNotJoined battelNotJoined) {
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(Map.of("error",battelNotJoined.getMessage()));
    }


}
