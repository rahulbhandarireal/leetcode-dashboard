package com.example.LeetDeCode_Battle_Module.exceptionhandler;


import com.example.LeetDeCode_Battle_Module.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    @ExceptionHandler(RoomFullException.class)
    public ResponseEntity<?> handleRoomFull(RoomFullException roomFullException) {
        return ResponseEntity
                .status(409)
                .body(Map.of("error",roomFullException.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error",ex.getMessage())); // 409
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(false);
        response.setMessage(ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed"));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RoomClosed.class)
    public ResponseEntity<?> hadnleroomclose(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error",ex.getMessage())); // 409
    }



}
