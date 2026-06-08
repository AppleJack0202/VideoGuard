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
import com.videoguard.entity.AiReviewResult;
import com.videoguard.entity.SensitiveHit;
import com.videoguard.entity.SensitiveWord;
import com.videoguard.entity.Video;
import com.videoguard.entity.VideoFrame;
import com.videoguard.repository.AiReviewResultRepository;
import com.videoguard.repository.ReviewLogRepository;
import com.videoguard.repository.SensitiveHitRepository;
import com.videoguard.repository.SensitiveWordRepository;
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
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Path uploadsRoot;
    private final Path projectRoot;

    public VideoService(
            VideoRepository videoRepository,
            SensitiveWordRepository sensitiveWordRepository,
            VideoFrameRepository videoFrameRepository,
            SensitiveHitRepository sensitiveHitRepository,
            AiReviewResultRepository aiReviewResultRepository,
            ReviewLogRepository reviewLogRepository,
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
        this.restClient = restClientBuilder.baseUrl(aiServiceBaseUrl).build();
        this.objectMapper = objectMapper;
        this.uploadsRoot = Path.of(uploadsDir).toAbsolutePath().normalize();
        this.projectRoot = uploadsRoot.getParent();
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
        VideoDetailResponse response = VideoDetailResponse.from(video);
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
                .map(ReviewLogResponse::from)
                .toList());
        return response;
    }

    @Transactional
    public VideoDetailResponse analyze(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + id));
        video.setStatus("PROCESSING");
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

        video.setAiRiskLevel(aiResponse.getRiskLevel());
        video.setAiRiskScore(aiResponse.getScores().getFinalScore());
        video.setStatus(toVideoStatus(aiResponse.getRiskLevel()));
        videoRepository.save(video);

        return detail(id);
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
                projectRoot.resolve(video.getFilePath()).toAbsolutePath().normalize().toString(),
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
            case "PASS" -> "AI_PASSED";
            case "SUSPICIOUS" -> "AI_SUSPICIOUS";
            case "VIOLATION" -> "AI_VIOLATION";
            default -> "FAILED";
        };
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
