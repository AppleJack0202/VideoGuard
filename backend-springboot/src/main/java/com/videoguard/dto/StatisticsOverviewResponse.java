package com.videoguard.dto;

public class StatisticsOverviewResponse {

    private Long totalVideos;
    private Long todayUploads;
    private Long pendingReviews;
    private Long manualReviewed;
    private Long aiPassed;
    private Double aiPassRate;

    public Long getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(Long totalVideos) {
        this.totalVideos = totalVideos;
    }

    public Long getTodayUploads() {
        return todayUploads;
    }

    public void setTodayUploads(Long todayUploads) {
        this.todayUploads = todayUploads;
    }

    public Long getPendingReviews() {
        return pendingReviews;
    }

    public void setPendingReviews(Long pendingReviews) {
        this.pendingReviews = pendingReviews;
    }

    public Long getManualReviewed() {
        return manualReviewed;
    }

    public void setManualReviewed(Long manualReviewed) {
        this.manualReviewed = manualReviewed;
    }

    public Long getAiPassed() {
        return aiPassed;
    }

    public void setAiPassed(Long aiPassed) {
        this.aiPassed = aiPassed;
    }

    public Double getAiPassRate() {
        return aiPassRate;
    }

    public void setAiPassRate(Double aiPassRate) {
        this.aiPassRate = aiPassRate;
    }
}
