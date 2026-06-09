package com.videoguard.dto;

import com.videoguard.entity.Video;
import java.time.LocalDateTime;

public class VideoListItemResponse {

    private Long id;
    private String title;
    private Long uploaderId;
    private LocalDateTime createdAt;
    private String status;
    private String aiRiskLevel;
    private Double aiRiskScore;
    private String violationCategory;
    private Long fileSize;
    private Double duration;

    public static VideoListItemResponse from(Video video) {
        VideoListItemResponse response = new VideoListItemResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setUploaderId(video.getUploaderId());
        response.setCreatedAt(video.getCreatedAt());
        response.setStatus(video.getStatus());
        response.setAiRiskLevel(video.getAiRiskLevel());
        response.setAiRiskScore(video.getAiRiskScore());
        response.setViolationCategory(video.getViolationCategory());
        response.setFileSize(video.getFileSize());
        response.setDuration(video.getDuration());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAiRiskLevel() {
        return aiRiskLevel;
    }

    public void setAiRiskLevel(String aiRiskLevel) {
        this.aiRiskLevel = aiRiskLevel;
    }

    public Double getAiRiskScore() {
        return aiRiskScore;
    }

    public void setAiRiskScore(Double aiRiskScore) {
        this.aiRiskScore = aiRiskScore;
    }

    public String getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(String violationCategory) {
        this.violationCategory = violationCategory;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
