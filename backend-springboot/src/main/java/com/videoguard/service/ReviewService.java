package com.videoguard.service;

import com.videoguard.dto.ReviewLogResponse;
import com.videoguard.dto.ReviewSubmitRequest;
import com.videoguard.dto.VideoDetailResponse;
import com.videoguard.dto.VideoListItemResponse;
import com.videoguard.entity.ReviewLog;
import com.videoguard.entity.Video;
import com.videoguard.repository.ReviewLogRepository;
import com.videoguard.repository.VideoRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ReviewService {

    private static final Set<String> REVIEWABLE_STATUSES = Set.of("AI_SUSPICIOUS", "AI_VIOLATION");
    private static final Set<String> FINAL_RESULTS = Set.of("PASS", "REJECT");

    private final VideoRepository videoRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final VideoService videoService;

    public ReviewService(
            VideoRepository videoRepository,
            ReviewLogRepository reviewLogRepository,
            VideoService videoService) {
        this.videoRepository = videoRepository;
        this.reviewLogRepository = reviewLogRepository;
        this.videoService = videoService;
    }

    @Transactional(readOnly = true)
    public List<VideoListItemResponse> tasks(String status, String aiRiskLevel) {
        Specification<Video> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            } else {
                predicates.add(root.get("status").in(REVIEWABLE_STATUSES));
            }
            if (StringUtils.hasText(aiRiskLevel)) {
                predicates.add(criteriaBuilder.equal(root.get("aiRiskLevel"), aiRiskLevel));
            }
            predicates.add(criteriaBuilder.isNull(root.get("finalResult")));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return videoRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "aiRiskScore")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(VideoListItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VideoDetailResponse taskDetail(Long videoId) {
        return videoService.detail(videoId);
    }

    @Transactional
    public VideoDetailResponse submit(Long videoId, ReviewSubmitRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + videoId));
        String finalResult = normalizeResult(request.getFinalResult());
        String comment = request.getComment() == null ? "" : request.getComment().trim();
        if (!StringUtils.hasText(comment)) {
            throw new IllegalArgumentException("Review comment is required.");
        }

        String beforeStatus = video.getStatus();
        String beforeResult = video.getFinalResult();
        String afterStatus = toManualStatus(finalResult);

        video.setFinalResult(finalResult);
        video.setFinalComment(comment);
        video.setStatus(afterStatus);
        videoRepository.save(video);

        ReviewLog log = new ReviewLog();
        log.setVideoId(videoId);
        log.setReviewerId(request.getReviewerId());
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setBeforeResult(beforeResult);
        log.setAfterResult(finalResult);
        log.setComment(comment);
        reviewLogRepository.save(log);

        return videoService.detail(videoId);
    }

    @Transactional(readOnly = true)
    public List<ReviewLogResponse> logs(Long videoId) {
        return reviewLogRepository.findByVideoIdOrderByCreatedAtDesc(videoId)
                .stream()
                .map(ReviewLogResponse::from)
                .toList();
    }

    private String normalizeResult(String finalResult) {
        if (finalResult == null) {
            throw new IllegalArgumentException("Final result is required.");
        }
        String normalized = finalResult.trim().toUpperCase(Locale.ROOT);
        if (!FINAL_RESULTS.contains(normalized)) {
            throw new IllegalArgumentException("Final result must be PASS or REJECT.");
        }
        return normalized;
    }

    private String toManualStatus(String finalResult) {
        return "PASS".equals(finalResult) ? "MANUAL_PASSED" : "MANUAL_REJECTED";
    }
}
