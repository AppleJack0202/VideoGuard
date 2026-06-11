package com.videoguard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoguard.dto.AiAnalyzeResponse;
import com.videoguard.dto.AiReviewResultResponse;
import com.videoguard.dto.ReviewLogResponse;
import com.videoguard.dto.SensitiveHitResponse;
import com.videoguard.dto.VideoDetailResponse;
import com.videoguard.dto.VideoFrameResponse;
import com.videoguard.dto.VideoListItemResponse;
import com.videoguard.dto.VideoUploadResponse;
import com.videoguard.config.UploadPathResolver;
import com.videoguard.entity.AiReviewResult;
import com.videoguard.entity.SensitiveHit;
import com.videoguard.entity.SensitiveWord;
import com.videoguard.entity.User;
import com.videoguard.entity.Video;
import com.videoguard.entity.VideoFrame;
import com.videoguard.repository.AiReviewResultRepository;
import com.videoguard.repository.ReviewLogRepository;
import com.videoguard.repository.SensitiveHitRepository;
import com.videoguard.repository.SensitiveWordRepository;
import com.videoguard.repository.UserRepository;
import com.videoguard.repository.VideoFrameRepository;
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
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp4", "mov", "avi");
    private static final Charset WINDOWS_1252 = Charset.forName("Windows-1252");

    private final VideoRepository videoRepository;
    private final SensitiveWordRepository sensitiveWordRepository;
    private final VideoFrameRepository videoFrameRepository;
    private final SensitiveHitRepository sensitiveHitRepository;
    private final AiReviewResultRepository aiReviewResultRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final UserRepository userRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Path uploadsRoot;
    private final String uploadsDir;

    public VideoService(
            VideoRepository videoRepository,
            SensitiveWordRepository sensitiveWordRepository,
            VideoFrameRepository videoFrameRepository,
            SensitiveHitRepository sensitiveHitRepository,
            AiReviewResultRepository aiReviewResultRepository,
            ReviewLogRepository reviewLogRepository,
            UserRepository userRepository,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${videoguard.uploads-dir:uploads}") String uploadsDir,
            @Value("${videoguard.ai-service.base-url:http://localhost:8000}") String aiServiceBaseUrl) {
        this.videoRepository = videoRepository;
        this.sensitiveWordRepository = sensitiveWordRepository;
        this.videoFrameRepository = videoFrameRepository;
        this.sensitiveHitRepository = sensitiveHitRepository;
        this.aiReviewResultRepository = aiReviewResultRepository;
        this.reviewLogRepository = reviewLogRepository;
        this.userRepository = userRepository;
        this.restClient = restClientBuilder.baseUrl(aiServiceBaseUrl).build();
        this.objectMapper = objectMapper;
        this.uploadsDir = uploadsDir;
        this.uploadsRoot = UploadPathResolver.resolve(uploadsDir);
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
            video.setStatus(WorkflowConstants.STATUS_UPLOADED);

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
    public List<VideoListItemResponse> list(String status, String aiRiskLevel, String violationCategory, Long uploaderId) {
        Specification<Video> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(aiRiskLevel)) {
                predicates.add(criteriaBuilder.equal(root.get("aiRiskLevel"), aiRiskLevel));
            }
            if (StringUtils.hasText(violationCategory)) {
                predicates.add(criteriaBuilder.equal(root.get("violationCategory"), violationCategory));
            }
            if (uploaderId != null) {
                predicates.add(criteriaBuilder.equal(root.get("uploaderId"), uploaderId));
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
        VideoDetailResponse response = VideoDetailResponse.from(video);
        applyUploader(response, video.getUploaderId());
        response.setFrames(videoFrameRepository.findByVideoIdOrderByTimestampSecAsc(id)
                .stream()
                .map(VideoFrameResponse::from)
                .toList());
        response.setSensitiveHits(sensitiveHitRepository.findByVideoIdOrderByCreatedAtAsc(id)
                .stream()
                .map(SensitiveHitResponse::from)
                .toList());
        response.setAiResult(aiReviewResultRepository.findTopByVideoIdOrderByCreatedAtDesc(id)
                .map(AiReviewResultResponse::from)
                .orElse(null));
        response.setReviewLogs(reviewLogRepository.findByVideoIdOrderByCreatedAtDesc(id)
                .stream()
                .map(this::toReviewLogResponse)
                .toList());
        return response;
    }

    private ReviewLogResponse toReviewLogResponse(com.videoguard.entity.ReviewLog log) {
        User reviewer = userRepository.findById(log.getReviewerId()).orElse(null);
        return ReviewLogResponse.from(log, reviewer);
    }

    @Transactional(readOnly = true)
    public VideoDetailResponse userDetail(Long id, Long uploaderId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + id));
        if (!video.getUploaderId().equals(uploaderId)) {
            throw new IllegalArgumentException("Video not found: " + id);
        }
        VideoDetailResponse response = VideoDetailResponse.from(video);
        applyUploader(response, video.getUploaderId());
        response.setAiRiskLevel(null);
        response.setAiRiskScore(null);
        response.setViolationCategory(null);
        response.setFinalResult(null);
        response.setFinalComment(null);
        response.setAiResult(null);
        response.setFrames(List.of());
        response.setSensitiveHits(List.of());
        response.setReviewLogs(List.of());
        return response;
    }

    private void applyUploader(VideoDetailResponse response, Long uploaderId) {
        if (uploaderId == null) {
            return;
        }
        userRepository.findById(uploaderId).ifPresent(user -> {
            response.setUploaderUsername(user.getUsername());
            response.setUploaderDisplayName(user.getDisplayName());
        });
    }

    @Transactional
    public VideoDetailResponse analyze(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + id));
        video.setStatus(WorkflowConstants.STATUS_PRE_REVIEWING);
        videoRepository.save(video);

        List<SensitiveWord> sensitiveWords = sensitiveWordRepository.findByEnabled(1);
        AiAnalyzeResponse.AnalyzeRequestPayload requestPayload = buildAnalyzeRequest(video, sensitiveWords);
        AiAnalyzeResponse aiResponse = restClient.post()
                .uri("/ai/analyze")
                .body(requestPayload)
                .retrieve()
                .body(AiAnalyzeResponse.class);

        if (aiResponse == null || aiResponse.getScores() == null) {
            throw new IllegalStateException("AI service returned empty analysis result.");
        }

        clearPreviousAnalysis(video.getId());
        saveMetadata(video, aiResponse);
        saveFrames(video.getId(), aiResponse);
        saveSensitiveHits(video.getId(), aiResponse);
        saveAiReviewResult(video.getId(), aiResponse);

        String riskLevel = WorkflowConstants.normalizeRiskLevel(aiResponse.getRiskLevel());
        video.setAiRiskLevel(riskLevel);
        video.setAiRiskScore(aiResponse.getScores().getFinalScore());
        video.setViolationCategory(resolveViolationCategory(aiResponse));
        video.setStatus(toVideoStatus(riskLevel));
        video.setFinalResult(toAiFinalResult(riskLevel));
        videoRepository.save(video);

        return detail(id);
    }

    @Transactional
    public void analyzeNextUploadedBatch(int limit) {
        List<Video> videos = videoRepository.findByStatusOrderByCreatedAtAsc(WorkflowConstants.STATUS_UPLOADED);
        videos.stream()
                .limit(Math.max(1, limit))
                .forEach(video -> analyze(video.getId()));
    }

    private AiAnalyzeResponse.AnalyzeRequestPayload buildAnalyzeRequest(Video video, List<SensitiveWord> sensitiveWords) {
        List<AiAnalyzeResponse.SensitiveWordPayload> words = sensitiveWords.stream()
                .map(word -> new AiAnalyzeResponse.SensitiveWordPayload(
                        word.getWord(),
                        word.getCategory(),
                        word.getWeight()))
                .toList();

        return new AiAnalyzeResponse.AnalyzeRequestPayload(
                video.getId(),
                UploadPathResolver.resolveStoredPath(uploadsDir, video.getFilePath()).toAbsolutePath().normalize().toString(),
                video.getTitle(),
                video.getDescription() == null ? "" : video.getDescription(),
                5,
                words);
    }

    private void clearPreviousAnalysis(Long videoId) {
        videoFrameRepository.deleteByVideoId(videoId);
        sensitiveHitRepository.deleteByVideoId(videoId);
        aiReviewResultRepository.deleteByVideoId(videoId);
    }

    private void saveMetadata(Video video, AiAnalyzeResponse aiResponse) {
        AiAnalyzeResponse.Metadata metadata = aiResponse.getMetadata();
        if (metadata == null) {
            return;
        }
        video.setDuration(metadata.getDuration());
        video.setWidth(metadata.getWidth());
        video.setHeight(metadata.getHeight());
        video.setFps(metadata.getFps());
        if (metadata.getFileSize() != null) {
            video.setFileSize(metadata.getFileSize());
        }
    }

    private void saveFrames(Long videoId, AiAnalyzeResponse aiResponse) {
        if (aiResponse.getFrames() == null) {
            return;
        }
        List<VideoFrame> frames = aiResponse.getFrames().stream()
                .map(frameResult -> {
                    VideoFrame frame = new VideoFrame();
                    frame.setVideoId(videoId);
                    frame.setFramePath(frameResult.getFramePath());
                    frame.setTimestampSec(frameResult.getTimestampSec());
                    frame.setLabel(frameResult.getLabel());
                    frame.setConfidence(frameResult.getConfidence());
                    frame.setRiskScore(frameResult.getRiskScore());
                    return frame;
                })
                .toList();
        videoFrameRepository.saveAll(frames);
    }

    private void saveSensitiveHits(Long videoId, AiAnalyzeResponse aiResponse) {
        if (aiResponse.getTextHits() == null) {
            return;
        }
        List<SensitiveHit> hits = aiResponse.getTextHits().stream()
                .map(textHit -> {
                    SensitiveHit hit = new SensitiveHit();
                    hit.setVideoId(videoId);
                    hit.setSourceType(textHit.getSourceType());
                    hit.setWord(textHit.getWord());
                    hit.setCategory(textHit.getCategory());
                    hit.setWeight(textHit.getWeight());
                    hit.setContextText(textHit.getContextText());
                    return hit;
                })
                .toList();
        sensitiveHitRepository.saveAll(hits);
    }

    private void saveAiReviewResult(Long videoId, AiAnalyzeResponse aiResponse) {
        AiAnalyzeResponse.Scores scores = aiResponse.getScores();
        AiReviewResult result = new AiReviewResult();
        result.setVideoId(videoId);
        result.setTextScore(scores.getTextScore());
        result.setImageScore(scores.getImageScore());
        result.setAsrScore(scores.getAsrScore());
        result.setFinalScore(scores.getFinalScore());
        result.setRiskLevel(aiResponse.getRiskLevel());
        result.setAsrText(aiResponse.getAsrText());
        result.setRawResultJson(toJson(aiResponse));
        aiReviewResultRepository.save(result);
    }

    private String toJson(AiAnalyzeResponse aiResponse) {
        try {
            return objectMapper.writeValueAsString(aiResponse);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize AI result.", e);
        }
    }

    private String toVideoStatus(String riskLevel) {
        return switch (riskLevel) {
            case WorkflowConstants.RISK_NORMAL -> WorkflowConstants.STATUS_PASSED;
            case WorkflowConstants.RISK_SUSPICIOUS, WorkflowConstants.RISK_VIOLATION -> WorkflowConstants.STATUS_MANUAL_REVIEWING;
            default -> WorkflowConstants.STATUS_APPEAL_PENDING;
        };
    }

    private String toAiFinalResult(String riskLevel) {
        return switch (riskLevel) {
            case WorkflowConstants.RISK_NORMAL -> WorkflowConstants.RISK_NORMAL;
            case WorkflowConstants.RISK_SUSPICIOUS -> WorkflowConstants.RISK_SUSPICIOUS;
            case WorkflowConstants.RISK_VIOLATION -> WorkflowConstants.RISK_VIOLATION;
            default -> null;
        };
    }

    private String resolveViolationCategory(AiAnalyzeResponse aiResponse) {
        if (aiResponse.getTextHits() != null && !aiResponse.getTextHits().isEmpty()) {
            return WorkflowConstants.normalizeCategory(aiResponse.getTextHits().get(0).getCategory());
        }
        if (aiResponse.getFrames() != null) {
            return aiResponse.getFrames().stream()
                    .filter(frame -> frame.getRiskScore() != null && frame.getRiskScore() >= 30)
                    .map(frame -> WorkflowConstants.normalizeCategory(frame.getLabel()))
                    .filter(StringUtils::hasText)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
