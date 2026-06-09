package com.videoguard.service;

import com.videoguard.dto.CountItemResponse;
import com.videoguard.dto.StatisticsOverviewResponse;
import com.videoguard.repository.VideoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

    private static final Set<String> AI_PRE_REVIEW_STATUSES = Set.of(
            WorkflowConstants.STATUS_UPLOADED,
            WorkflowConstants.STATUS_PRE_REVIEWING);
    private static final Set<String> REVIEWED_STATUSES = Set.of(
            WorkflowConstants.STATUS_PASSED,
            WorkflowConstants.STATUS_REJECTED,
            WorkflowConstants.STATUS_APPEAL_PENDING);

    private final VideoRepository videoRepository;

    public StatisticsService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Transactional(readOnly = true)
    public StatisticsOverviewResponse overview() {
        long total = videoRepository.count();
        long todayUploads = videoRepository.countByCreatedAtGreaterThanEqual(LocalDate.now().atStartOfDay());
        long pendingReviews = videoRepository.countByStatus(WorkflowConstants.STATUS_MANUAL_REVIEWING);
        long manualReviewed = videoRepository.countByStatusIn(REVIEWED_STATUSES);
        long aiPassed = videoRepository.countByStatus(WorkflowConstants.STATUS_PASSED);
        long preReviewTotal = videoRepository.countByStatusIn(AI_PRE_REVIEW_STATUSES);
        long preReviewNormal = videoRepository.countByStatusInAndAiRiskLevel(
                AI_PRE_REVIEW_STATUSES,
                WorkflowConstants.RISK_NORMAL);

        StatisticsOverviewResponse response = new StatisticsOverviewResponse();
        response.setTotalVideos(total);
        response.setTodayUploads(todayUploads);
        response.setPendingReviews(pendingReviews);
        response.setManualReviewed(manualReviewed);
        response.setAiPassed(aiPassed);
        response.setAiPassRate(preReviewTotal == 0 ? 0.0 : roundPercent(preReviewNormal * 100.0 / preReviewTotal));
        return response;
    }

    @Transactional(readOnly = true)
    public List<CountItemResponse> riskDistribution() {
        return videoRepository.countByRiskLevel();
    }

    @Transactional(readOnly = true)
    public List<CountItemResponse> statusDistribution() {
        return videoRepository.countByStatusGroup();
    }

    @Transactional(readOnly = true)
    public List<CountItemResponse> dailyUploads(int days) {
        int normalizedDays = Math.max(1, Math.min(days, 30));
        LocalDateTime start = LocalDate.now().minusDays(normalizedDays - 1L).atStartOfDay();
        return videoRepository.countDailyUploads(start);
    }

    @Transactional(readOnly = true)
    public List<CountItemResponse> categoryDistribution() {
        return videoRepository.countByViolationCategory();
    }

    private Double roundPercent(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
