package com.videoguard.service;

import com.videoguard.dto.VideoDetailResponse;
import com.videoguard.dto.VideoListItemResponse;
import com.videoguard.dto.VideoUploadResponse;
import com.videoguard.entity.Video;
import com.videoguard.repository.VideoRepository;
import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
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
        if (!StringUtils.hasText(title)) {
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
            video.setTitle(title.trim());
            video.setDescription(description);
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

