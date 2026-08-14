package com.example.LeetDeCode_Battle_Module.enums;

public enum Language {
    PYTHON(
            "python:3.11-alpine",
            "Main.py",
            "python3 /code/Main.py"
    ),
    JAVA(
            "eclipse-temurin:17-alpine",
            "Main.java",
            "javac /code/Main.java -d /tmp && java -cp /tmp Main"
    ),
    CPP(
            "frolvlad/alpine-gxx",
            "Main.cpp",
            "g++ /code/Main.cpp -O2 -o /tmp/a.out && /tmp/a.out"
    );

    private final String dockerImage;
    private final String fileName;
    private final String runCommand;

    Language(String dockerImage, String fileName, String runCommand) {
        this.dockerImage = dockerImage;
        this.fileName = fileName;
        this.runCommand = runCommand;
    }

    public String getDockerImage() { return dockerImage; }
    public String getFileName() { return fileName; }
    public String getRunCommand() { return runCommand; }

    public static Language fromString(String value) {
        return Language.valueOf(value.trim().toUpperCase());
    }
}