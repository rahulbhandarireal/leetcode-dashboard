package com.example.LeetDeCode_Battle_Module.util;

public final class RedisKeyUtil {
    private RedisKeyUtil() {}

    public static String roomKey(String roomCode) {
        return "room:" + roomCode.toUpperCase();
    }

    public static String roomViewKey(String roomCode) {
        return "room:view:" + roomCode.toUpperCase();
    }

    public static String problemKey(int problemId) {
        return "problem:" + problemId;
    }
}
