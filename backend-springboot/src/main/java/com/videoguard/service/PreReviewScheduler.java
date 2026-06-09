package com.videoguard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PreReviewScheduler {

    private final VideoService videoService;
    private final boolean enabled;
    private final int batchSize;

    public PreReviewScheduler(
            VideoService videoService,
            @Value("${videoguard.pre-review.enabled:true}") boolean enabled,
            @Value("${videoguard.pre-review.batch-size:2}") int batchSize) {
        this.videoService = videoService;
        this.enabled = enabled;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${videoguard.pre-review.fixed-delay-ms:15000}")
    public void analyzeUploadedVideos() {
        if (!enabled) {
            return;
        }
        videoService.analyzeNextUploadedBatch(batchSize);
    }
}
