package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.enums.Language;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

@Service
public class DockerRunnerService {

    private static final Logger log = LoggerFactory.getLogger(DockerRunnerService.class);
    private static final int TIMEOUT_SECONDS = 5;

    // Must match the host-side bind mount path in docker-compose.yml
    private static final Path SUBMISSIONS_BASE = Path.of("/battle-submissions");

    @PostConstruct
    public void prewarmDockerImages() {
        // Ensure the shared directory exists before anything tries to use it
        try {
            Files.createDirectories(SUBMISSIONS_BASE);
        } catch (IOException e) {
            log.error("Failed to create submissions base directory", e);
        }

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
            // Create temp dir INSIDE the shared bind-mounted path, not the JVM's default tmp
            tempDir = Files.createTempDirectory(SUBMISSIONS_BASE, "submission-");
            Path codeFile = tempDir.resolve(language.getFileName());
            Files.writeString(codeFile, code);

            tempDir.toFile().setReadable(true, false);
            tempDir.toFile().setWritable(true, false);
            tempDir.toFile().setExecutable(true, false);
            codeFile.toFile().setReadable(true, false);

            // Compute the HOST path that corresponds to this container path,
            // since -v needs to be resolved by the HOST Docker daemon.
            String hostSubmissionsBase = System.getenv()
                    .getOrDefault("HOST_SUBMISSIONS_PATH", "/home/ubuntu/battle-submissions");
            String relativeDirName = tempDir.getFileName().toString();
            String hostTempDirPath = hostSubmissionsBase + "/" + relativeDirName;

            List<String> command = new ArrayList<>(List.of(
                    "docker", "run", "--rm", "-i",
                    "--name", containerName,
                    "--memory=256m",
                    "--cpus=1.0",
                    "--network=none",
                    "--pids-limit=64",
                    "-v", hostTempDirPath + ":/code",
                    language.getDockerImage(),
                    "sh", "-c", language.getRunCommand()));

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
                } catch (IOException ignored) {
                }
            }
        }
    }

    private void killContainer(String containerName) {
        try {
            new ProcessBuilder("docker", "kill", containerName).start().waitFor(2, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException ignored) {
        }
    }
}