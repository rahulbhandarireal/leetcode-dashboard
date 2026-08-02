package com.example.LeetDeCode_Battle_Module.exceptionhandler;

public class RoomClosed extends RuntimeException {
    public RoomClosed(String message) {
        super(message);
    }
}
