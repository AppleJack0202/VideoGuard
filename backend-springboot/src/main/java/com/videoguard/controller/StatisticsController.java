package com.videoguard.controller;

import com.videoguard.dto.CountItemResponse;
import com.videoguard.dto.StatisticsOverviewResponse;
import com.videoguard.service.StatisticsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public StatisticsOverviewResponse overview() {
        return statisticsService.overview();
    }

    @GetMapping("/risk-distribution")
    public List<CountItemResponse> riskDistribution() {
        return statisticsService.riskDistribution();
    }

    @GetMapping("/status-distribution")
    public List<CountItemResponse> statusDistribution() {
        return statisticsService.statusDistribution();
    }

    @GetMapping("/daily-upload")
    public List<CountItemResponse> dailyUploads(@RequestParam(value = "days", defaultValue = "7") int days) {
        return statisticsService.dailyUploads(days);
    }

    @GetMapping("/category-distribution")
    public List<CountItemResponse> categoryDistribution() {
        return statisticsService.categoryDistribution();
    }
}
