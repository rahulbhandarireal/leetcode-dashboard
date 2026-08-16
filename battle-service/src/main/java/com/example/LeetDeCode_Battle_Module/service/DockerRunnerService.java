package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.enums.Language;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class DockerRunnerService {

    private static final Logger log = LoggerFactory.getLogger(DockerRunnerService.class);
    private static final int TIMEOUT_SECONDS = 5;

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private final ExecutorService streamReadExecutor = Executors.newCachedThreadPool();

    private static final Path SUBMISSIONS_BASE;

    static {
        String containerPath = System.getenv("CONTAINER_SUBMISSIONS_PATH");
        if (containerPath != null && !containerPath.isBlank()) {
            SUBMISSIONS_BASE = Paths.get(containerPath);
        } else {
            SUBMISSIONS_BASE = Paths.get(System.getProperty("user.home"), "battle-submissions");
        }
    }

    @PostConstruct
    public void prewarmDockerImages() {
        try {
            log.info("Initialized DockerRunnerService with SUBMISSIONS_BASE: {}", SUBMISSIONS_BASE);
            if (!Files.exists(SUBMISSIONS_BASE)) {
                Files.createDirectories(SUBMISSIONS_BASE);
                setPermissions(SUBMISSIONS_BASE);
            }
        } catch (IOException e) {
            log.error("Failed to create submissions base directory", e);
        }
    }

    public ExecutionResult run(Language language, String code, String input) {
        Path tempDir = null;
        String containerName = "exec-" + UUID.randomUUID();

        try {
            // Create a temp dir inside the shared submissions folder
            tempDir = Files.createTempDirectory(SUBMISSIONS_BASE, "submission-");
            setPermissions(tempDir);

            Path codeFile = tempDir.resolve(language.getFileName());
            Files.writeString(codeFile, code, StandardCharsets.UTF_8);
            setPermissions(codeFile);

            // Compute path to mount to Docker container
            String hostVolumePath = resolveHostVolumePath(tempDir);
            log.info("Running container {} with hostVolumePath: {} and internal codeFile: {}", containerName, hostVolumePath, codeFile);

            List<String> command = new ArrayList<>(List.of(
                    "docker", "run", "--rm", "-i",
                    "--name", containerName,
                    "--memory=256m",
                    "--cpus=1.0",
                    "--network=none",
                    "--pids-limit=64",
                    "--read-only",
                    "--tmpfs", "/tmp:rw,exec,nosuid,size=64m",
                    "-v", hostVolumePath + ":/code:ro",
                    language.getDockerImage(),
                    "sh", "-c", language.getRunCommand()
            ));

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            // Handle Standard Input (stdin)
            if (input != null && !input.isEmpty()) {
                try (OutputStream stdin = process.getOutputStream()) {
                    stdin.write(input.getBytes(StandardCharsets.UTF_8));
                    stdin.flush();
                } catch (IOException ignored) {}
            } else {
                process.getOutputStream().close();
            }

            // Async stream capture to avoid buffer lock/deadlocks
            Future<String> stdoutFuture = streamReadExecutor.submit(
                    () -> new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
            );
            Future<String> stderrFuture = streamReadExecutor.submit(
                    () -> new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8)
            );

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                killContainer(containerName);
                process.destroyForcibly();
                stdoutFuture.cancel(true);
                stderrFuture.cancel(true);
                return ExecutionResult.builder().timedOut(true).build();
            }

            String stdout = stdoutFuture.get(1, TimeUnit.SECONDS);
            String stderr = stderrFuture.get(1, TimeUnit.SECONDS);

            return ExecutionResult.builder()
                    .stdout(stdout.trim())
                    .stderr(stderr.trim())
                    .timedOut(false)
                    .build();

        } catch (Exception e) {
            log.error("Execution error on container {}", containerName, e);
            return ExecutionResult.builder()
                    .stderr("Internal error: " + e.getMessage())
                    .build();
        } finally {
            if (tempDir != null) {
                try {
                    FileSystemUtils.deleteRecursively(tempDir);
                } catch (IOException e) {
                    log.warn("Failed to delete temp dir: {}", tempDir, e);
                }
            }
        }
    }

    /**
     * Translates local OS file paths into Docker-compatible volume paths.
     */
    private String resolveHostVolumePath(Path targetDir) {
        String customHostPath = System.getenv("HOST_SUBMISSIONS_PATH");
        if (customHostPath != null && !customHostPath.isBlank()) {
            return customHostPath + "/" + targetDir.getFileName().toString();
        }

        String absolutePath = targetDir.toAbsolutePath().toString();

        if (IS_WINDOWS) {
            // Converts C:\Users\name\... to /c/Users/name/... or C:/Users/name/... for Docker CLI compatibility
            return absolutePath.replace("\\", "/");
        }

        return absolutePath;
    }

    /**
     * Cross-platform file permission setter.
     */
    private void setPermissions(Path path) {
        if (!IS_WINDOWS) {
            try {
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
                Files.setPosixFilePermissions(path, perms);
                return;
            } catch (Exception ignored) {}
        }

        // Fallback for Windows
        File file = path.toFile();
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);
    }

    private void killContainer(String containerName) {
        try {
            new ProcessBuilder("docker", "rm", "-f", containerName)
                    .start()
                    .waitFor(2, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException ignored) {}
    }
}