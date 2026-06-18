package com.videoguard.service;

import com.videoguard.dto.CountItemResponse;
import com.videoguard.dto.StatisticsOverviewResponse;
import com.videoguard.repository.VideoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

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
        long aiAnalyzedTotal = videoRepository.countByAiRiskLevelIsNotNull();
        long aiPassed = videoRepository.countByAiRiskLevel(WorkflowConstants.RISK_NORMAL);

        StatisticsOverviewResponse response = new StatisticsOverviewResponse();
        response.setTotalVideos(total);
        response.setTodayUploads(todayUploads);
        response.setPendingReviews(pendingReviews);
        response.setManualReviewed(manualReviewed);
        response.setAiPassed(aiPassed);
        response.setAiPassRate(aiAnalyzedTotal == 0 ? 0.0 : roundPercent(aiPassed * 100.0 / aiAnalyzedTotal));
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
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (CountItemResponse item : videoRepository.countByViolationCategory()) {
            String categories = WorkflowConstants.normalizeCategories(item.getName());
            if (categories == null || categories.isBlank()) {
                distribution.merge("未分类", item.getCount(), Long::sum);
                continue;
            }
            for (String category : categories.split(",")) {
                distribution.merge(category, item.getCount(), Long::sum);
            }
        }
        return distribution.entrySet().stream()
                .map(entry -> new CountItemResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private Double roundPercent(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
