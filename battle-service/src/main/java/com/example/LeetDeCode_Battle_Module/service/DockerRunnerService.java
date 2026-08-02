package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.enums.Language;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import jakarta.annotation.PostConstruct; // <-- ADD THIS
import org.slf4j.Logger;                 // <-- ADD THIS
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerRunnerService {

    private static final Logger log = LoggerFactory.getLogger(DockerRunnerService.class);
    private static final int TIMEOUT_SECONDS = 5;

    @PostConstruct
    public void prewarmDockerImages() {
        new Thread(() -> {
            for (Language lang : Language.values()) {
                try {
                    log.info("Pre-pulling Docker image: {}", lang.getDockerImage());
                    new ProcessBuilder("docker", "pull", lang.getDockerImage()).start().waitFor();
                } catch (Exception e) {
                    log.error("Failed to pre-warm image: {}", lang.getDockerImage(), e);
                }
            }
        }).start();
    }
    public ExecutionResult run(Language language, String code, String input) {
        Path tempDir = null;
        String containerName = "exec-" + UUID.randomUUID();

        try {
            // 1. Write the user's code to a file named correctly for this language
   		tempDir = Files.createTempDirectory("submission-");
Path codeFile = tempDir.resolve(language.getFileName());
Files.writeString(codeFile, code);

// Grant full read/write permissions so container process can compile files in /code
tempDir.toFile().setReadable(true, false); tempDir.toFile().setWritable(true, false); 
tempDir.toFile().setExecutable(true, false); codeFile.toFile().setReadable(true, false);

            // 2. Build the docker command — note "sh -c" wraps our compile&&run string
            List<String> command = new ArrayList<>(List.of(
                    "docker", "run", "--rm", "-i",
                    "--name", containerName,
                    "--memory=256m",
                    "--cpus=1.0",
                    "--network=none",
                    "--pids-limit=64",
                    "-v", tempDir.toAbsolutePath() + ":/code",
                    language.getDockerImage(),
                    "sh", "-c", language.getRunCommand()
            ));

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(input.getBytes());
            }

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                killContainer(containerName);
                process.destroyForcibly();
                return ExecutionResult.builder().timedOut(true).build();
            }

            String stdout = new String(process.getInputStream().readAllBytes());
            String stderr = new String(process.getErrorStream().readAllBytes());

            return ExecutionResult.builder()
                    .stdout(stdout.trim())
                    .stderr(stderr.trim())
                    .timedOut(false)
                    .build();

        } catch (IOException | InterruptedException e) {
            return ExecutionResult.builder()
                    .stderr("Internal error: " + e.getMessage())
                    .build();
        } finally {
            if (tempDir != null) {
                try {
                    FileSystemUtils.deleteRecursively(tempDir);
                } catch (IOException ignored) {}
            }
        }
    }

    private void killContainer(String containerName) {
        try {
            new ProcessBuilder("docker", "kill", containerName).start().waitFor(2, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException ignored) {}
    }
}
