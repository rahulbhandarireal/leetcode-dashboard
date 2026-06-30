package com.example.LeetDeCode_Battle_Module.enums;

public enum Language {
    PYTHON("python:3.11-slim", "Main.py", "python3 /code/Main.py"),
    JAVA("openjdk:17-slim", "Main.java", "javac /code/Main.java -d /code && java -cp /code Main"),
    CPP("gcc:13", "Main.cpp", "g++ /code/Main.cpp -o /code/a.out && /code/a.out");

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