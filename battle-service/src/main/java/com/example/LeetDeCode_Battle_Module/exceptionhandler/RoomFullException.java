package com.example.LeetDeCode_Battle_Module.exceptionhandler;

public class RoomFullException extends RuntimeException {
    public RoomFullException(String message) {
        super(message);
    }
}
