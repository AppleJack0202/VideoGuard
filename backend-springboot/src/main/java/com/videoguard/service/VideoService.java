package com.videoguard.service;

import com.videoguard.dto.VideoDetailResponse;
import com.videoguard.dto.VideoListItemResponse;
import com.videoguard.dto.VideoUploadResponse;
import com.videoguard.entity.Video;
import com.videoguard.repository.VideoRepository;
import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp4", "mov", "avi");
    private static final Charset WINDOWS_1252 = Charset.forName("Windows-1252");

    private final VideoRepository videoRepository;
    private final Path uploadsRoot;

    public VideoService(
            VideoRepository videoRepository,
            @Value("${videoguard.uploads-dir:uploads}") String uploadsDir) {
        this.videoRepository = videoRepository;
        this.uploadsRoot = Path.of(uploadsDir).toAbsolutePath().normalize();
    }

    @Transactional
    public VideoUploadResponse upload(MultipartFile file, String title, String description, Long uploaderId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Video file is required.");
        }
        String normalizedTitle = normalizeMultipartText(title);
        String normalizedDescription = normalizeMultipartText(description);

        if (!StringUtils.hasText(normalizedTitle)) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (uploaderId == null) {
            throw new IllegalArgumentException("Uploader ID is required.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "video" : file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only mp4, mov, and avi files are allowed.");
        }

        Path videoDir = uploadsRoot.resolve("videos");
        try {
            Files.createDirectories(videoDir);
            String storedFilename = UUID.randomUUID() + "." + extension;
            Path storedPath = videoDir.resolve(storedFilename).normalize();
            file.transferTo(storedPath);

            Video video = new Video();
            video.setUploaderId(uploaderId);
            video.setTitle(normalizedTitle.trim());
            video.setDescription(normalizedDescription);
            video.setOriginalFilename(originalFilename);
            video.setStoredFilename(storedFilename);
            video.setFilePath(Path.of("uploads", "videos", storedFilename).toString().replace("\\", "/"));
            video.setFileSize(file.getSize());
            video.setStatus("UPLOADED");

            return VideoUploadResponse.from(videoRepository.save(video));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save uploaded video.", e);
        }
    }

    private String normalizeMultipartText(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (containsCjk(value) || !looksLikeMojibake(value)) {
            return value;
        }
        return new String(value.getBytes(WINDOWS_1252), StandardCharsets.UTF_8);
    }

    private boolean containsCjk(String value) {
        for (int i = 0; i < value.length(); i++) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(value.charAt(i));
            if (block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
                return true;
            }
        }
        return false;
    }

    private boolean looksLikeMojibake(String value) {
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch >= '\u00c0' && ch <= '\u00ff') {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<VideoListItemResponse> list(String status, String aiRiskLevel) {
        Specification<Video> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(aiRiskLevel)) {
                predicates.add(criteriaBuilder.equal(root.get("aiRiskLevel"), aiRiskLevel));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return videoRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(VideoListItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VideoDetailResponse detail(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + id));
        return VideoDetailResponse.from(video);
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
