package com.videoguard.service;

import com.videoguard.dto.CountItemResponse;
import com.videoguard.dto.StatisticsOverviewResponse;
import com.videoguard.repository.SensitiveHitRepository;
import com.videoguard.repository.VideoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

    private static final Set<String> REVIEWABLE_STATUSES = Set.of("AI_SUSPICIOUS", "AI_VIOLATION");
    private static final Set<String> MANUAL_STATUSES = Set.of("MANUAL_PASSED", "MANUAL_REJECTED");

    private final VideoRepository videoRepository;
    private final SensitiveHitRepository sensitiveHitRepository;

    public StatisticsService(VideoRepository videoRepository, SensitiveHitRepository sensitiveHitRepository) {
        this.videoRepository = videoRepository;
        this.sensitiveHitRepository = sensitiveHitRepository;
    }

    @Transactional(readOnly = true)
    public StatisticsOverviewResponse overview() {
        long total = videoRepository.count();
        long todayUploads = videoRepository.countByCreatedAtGreaterThanEqual(LocalDate.now().atStartOfDay());
        long pendingReviews = videoRepository.countByStatusInAndFinalResultIsNull(REVIEWABLE_STATUSES);
        long manualReviewed = videoRepository.countByStatusIn(MANUAL_STATUSES);
        long aiPassed = videoRepository.countByStatus("AI_PASSED");

        StatisticsOverviewResponse response = new StatisticsOverviewResponse();
        response.setTotalVideos(total);
        response.setTodayUploads(todayUploads);
        response.setPendingReviews(pendingReviews);
        response.setManualReviewed(manualReviewed);
        response.setAiPassed(aiPassed);
        response.setAiPassRate(total == 0 ? 0.0 : roundPercent(aiPassed * 100.0 / total));
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
        return sensitiveHitRepository.countByCategory();
    }

    private Double roundPercent(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
