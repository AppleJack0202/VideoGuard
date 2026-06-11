package com.videoguard.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class UploadPathResolver {

    private UploadPathResolver() {
    }

    public static Path resolve(String uploadsDir) {
        return candidateRoots(uploadsDir).get(0);
    }

    public static List<Path> candidateRoots(String uploadsDir) {
        String normalizedUploadsDir = uploadsDir == null || uploadsDir.isBlank() ? "uploads" : uploadsDir;
        Path configuredPath = Path.of(normalizedUploadsDir);
        List<Path> roots = new ArrayList<>();
        if (configuredPath.isAbsolute()) {
            addDistinct(roots, configuredPath.normalize());
            return roots;
        }

        Path workingDir = Path.of("").toAbsolutePath().normalize();
        Path projectRoot = findProjectRoot(workingDir);
        if (projectRoot != null && isDefaultUploadsPath(configuredPath)) {
            addDistinct(roots, projectRoot.resolve("uploads").normalize());
            if (projectRoot.getParent() != null) {
                addDistinct(roots, projectRoot.getParent().resolve("uploads").normalize());
            }
            return roots;
        }
        addDistinct(roots, workingDir.resolve(configuredPath).normalize());
        return roots;
    }

    public static Path resolveStoredPath(String uploadsDir, String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }
        Path relativePath = Path.of(storedPath).normalize();
        if (relativePath.isAbsolute()) {
            return relativePath;
        }
        if (relativePath.getNameCount() > 0 && "uploads".equals(relativePath.getName(0).toString())) {
            relativePath = relativePath.subpath(1, relativePath.getNameCount());
        }
        for (Path root : candidateRoots(uploadsDir)) {
            Path candidate = root.resolve(relativePath).normalize();
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return candidateRoots(uploadsDir).get(0).resolve(relativePath).normalize();
    }

    private static void addDistinct(List<Path> roots, Path path) {
        if (!roots.contains(path)) {
            roots.add(path);
        }
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
