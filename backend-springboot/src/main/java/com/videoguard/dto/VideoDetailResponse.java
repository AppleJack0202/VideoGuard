package com.videoguard.dto;

import com.videoguard.entity.Video;
import java.time.LocalDateTime;
import java.util.List;

public class VideoDetailResponse {

    private Long id;
    private Long uploaderId;
    private String uploaderUsername;
    private String uploaderDisplayName;
    private String title;
    private String description;
    private String originalFilename;
    private String storedFilename;
    private String filePath;
    private String fileUrl;
    private Long fileSize;
    private Double duration;
    private Integer width;
    private Integer height;
    private Double fps;
    private String status;
    private String aiRiskLevel;
    private Double aiRiskScore;
    private String violationCategory;
    private String contentCategory;
    private Double categoryConfidence;
    private String categoryReason;
    private String reviewStrategy;
    private String finalResult;
    private String finalComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AiReviewResultResponse aiResult;
    private List<VideoFrameResponse> frames;
    private List<SensitiveHitResponse> sensitiveHits;
    private List<ReviewLogResponse> reviewLogs;

    public static VideoDetailResponse from(Video video) {
        VideoDetailResponse response = new VideoDetailResponse();
        response.setId(video.getId());
        response.setUploaderId(video.getUploaderId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setOriginalFilename(video.getOriginalFilename());
        response.setStoredFilename(video.getStoredFilename());
        response.setFilePath(video.getFilePath());
        response.setFileUrl("/" + video.getFilePath().replace("\\", "/"));
        response.setFileSize(video.getFileSize());
        response.setDuration(video.getDuration());
        response.setWidth(video.getWidth());
        response.setHeight(video.getHeight());
        response.setFps(video.getFps());
        response.setStatus(video.getStatus());
        response.setAiRiskLevel(video.getAiRiskLevel());
        response.setAiRiskScore(video.getAiRiskScore());
        response.setViolationCategory(video.getViolationCategory());
        response.setContentCategory(video.getContentCategory());
        response.setCategoryConfidence(video.getCategoryConfidence());
        response.setCategoryReason(video.getCategoryReason());
        response.setReviewStrategy(video.getReviewStrategy());
        response.setFinalResult(video.getFinalResult());
        response.setFinalComment(video.getFinalComment());
        response.setCreatedAt(video.getCreatedAt());
        response.setUpdatedAt(video.getUpdatedAt());
        response.setFrames(List.of());
        response.setSensitiveHits(List.of());
        response.setReviewLogs(List.of());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderUsername() {
        return uploaderUsername;
    }

    public void setUploaderUsername(String uploaderUsername) {
        this.uploaderUsername = uploaderUsername;
    }

    public String getUploaderDisplayName() {
        return uploaderDisplayName;
    }

    public void setUploaderDisplayName(String uploaderDisplayName) {
        this.uploaderDisplayName = uploaderDisplayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getFps() {
        return fps;
    }

    public void setFps(Double fps) {
        this.fps = fps;
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

    public String getContentCategory() {
        return contentCategory;
    }

    public void setContentCategory(String contentCategory) {
        this.contentCategory = contentCategory;
    }

    public Double getCategoryConfidence() {
        return categoryConfidence;
    }

    public void setCategoryConfidence(Double categoryConfidence) {
        this.categoryConfidence = categoryConfidence;
    }

    public String getCategoryReason() {
        return categoryReason;
    }

    public void setCategoryReason(String categoryReason) {
        this.categoryReason = categoryReason;
    }

    public String getReviewStrategy() {
        return reviewStrategy;
    }

    public void setReviewStrategy(String reviewStrategy) {
        this.reviewStrategy = reviewStrategy;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(String finalResult) {
        this.finalResult = finalResult;
    }

    public String getFinalComment() {
        return finalComment;
    }

    public void setFinalComment(String finalComment) {
        this.finalComment = finalComment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AiReviewResultResponse getAiResult() {
        return aiResult;
    }

    public void setAiResult(AiReviewResultResponse aiResult) {
        this.aiResult = aiResult;
    }

    public List<VideoFrameResponse> getFrames() {
        return frames;
    }

    public void setFrames(List<VideoFrameResponse> frames) {
        this.frames = frames;
    }

    public List<SensitiveHitResponse> getSensitiveHits() {
        return sensitiveHits;
    }

    public void setSensitiveHits(List<SensitiveHitResponse> sensitiveHits) {
        this.sensitiveHits = sensitiveHits;
    }

    public List<ReviewLogResponse> getReviewLogs() {
        return reviewLogs;
    }

    public void setReviewLogs(List<ReviewLogResponse> reviewLogs) {
        this.reviewLogs = reviewLogs;
    }
}
