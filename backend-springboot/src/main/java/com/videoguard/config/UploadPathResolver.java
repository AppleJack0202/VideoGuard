package com.videoguard.config;

import java.nio.file.Files;
import java.nio.file.Path;

public final class UploadPathResolver {

    private UploadPathResolver() {
    }

    public static Path resolve(String uploadsDir) {
        String normalizedUploadsDir = uploadsDir == null || uploadsDir.isBlank() ? "uploads" : uploadsDir;
        Path configuredPath = Path.of(normalizedUploadsDir);
        if (configuredPath.isAbsolute()) {
            return configuredPath.normalize();
        }

        Path workingDir = Path.of("").toAbsolutePath().normalize();
        Path projectRoot = findProjectRoot(workingDir);
        if (projectRoot != null && isDefaultUploadsPath(configuredPath)) {
            return projectRoot.resolve("uploads").normalize();
        }
        return workingDir.resolve(configuredPath).normalize();
    }

    private static boolean isDefaultUploadsPath(Path path) {
        Path normalizedPath = path.normalize();
        return normalizedPath.equals(Path.of("uploads"))
                || normalizedPath.equals(Path.of("..", "uploads"));
    }

    private static Path findProjectRoot(Path start) {
        Path current = start;
        while (current != null) {
            if (isProjectRoot(current)) {
                return current;
            }
            if (current.getFileName() != null && "backend-springboot".equals(current.getFileName().toString())) {
                Path parent = current.getParent();
                if (parent != null && isProjectRoot(parent)) {
                    return parent;
                }
            }
            current = current.getParent();
        }
        return null;
    }

    private static boolean isProjectRoot(Path path) {
        return Files.isDirectory(path.resolve("backend-springboot"))
                && Files.isDirectory(path.resolve("frontend-vue"))
                && Files.isDirectory(path.resolve("ai-service-fastapi"));
    }
}
