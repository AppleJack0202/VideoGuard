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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ReviewService {

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
    public List<VideoListItemResponse> tasks(String status, String aiRiskLevel, String violationCategory) {
        Specification<Video> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            } else {
                predicates.add(root.get("status").in(WorkflowConstants.REVIEWABLE_STATUSES));
            }
            if (StringUtils.hasText(aiRiskLevel)) {
                predicates.add(criteriaBuilder.equal(root.get("aiRiskLevel"), aiRiskLevel));
            }
            if (StringUtils.hasText(violationCategory)) {
                predicates.add(criteriaBuilder.equal(root.get("violationCategory"), violationCategory));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return videoRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
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
        if (!WorkflowConstants.STATUS_MANUAL_REVIEWING.equals(video.getStatus())) {
            throw new IllegalArgumentException("Review has already been submitted or the video is not in manual review.");
        }
        String afterStatus = normalizeStatus(request.getStatus());
        String violationCategory = normalizeViolationCategory(request.getViolationCategory(), afterStatus);
        String finalResult = toFinalResult(afterStatus);
        String comment = request.getComment() == null ? "" : request.getComment().trim();
        if (!StringUtils.hasText(comment)) {
            throw new IllegalArgumentException("Review comment is required.");
        }

        String beforeStatus = video.getStatus();
        String beforeResult = video.getFinalResult();

        video.setFinalResult(finalResult);
        video.setFinalComment(comment);
        video.setStatus(afterStatus);
        video.setViolationCategory(violationCategory);
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

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("Review status is required.");
        }
        String normalized = status.trim();
        if (!WorkflowConstants.REVIEW_SUBMIT_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("Review status must be 通过, 驳回, or 待申诉.");
        }
        return normalized;
    }

    private String normalizeViolationCategory(String violationCategory, String status) {
        if (WorkflowConstants.STATUS_PASSED.equals(status)) {
            return null;
        }
        String normalized = WorkflowConstants.normalizeCategory(violationCategory);
        if (!StringUtils.hasText(normalized) || !WorkflowConstants.VIOLATION_CATEGORIES.contains(normalized)) {
            throw new IllegalArgumentException("Violation category must be 暴力, 色情, or 政治敏感.");
        }
        return normalized;
    }

    private String toFinalResult(String status) {
        return switch (status) {
            case WorkflowConstants.STATUS_PASSED -> WorkflowConstants.RISK_NORMAL;
            case WorkflowConstants.STATUS_APPEAL_PENDING -> WorkflowConstants.RISK_SUSPICIOUS;
            case WorkflowConstants.STATUS_REJECTED -> WorkflowConstants.RISK_VIOLATION;
            default -> WorkflowConstants.RISK_SUSPICIOUS;
        };
    }
}
