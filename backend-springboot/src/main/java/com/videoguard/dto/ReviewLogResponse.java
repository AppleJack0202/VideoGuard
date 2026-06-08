package com.videoguard.dto;

import com.videoguard.entity.ReviewLog;
import java.time.LocalDateTime;

public class ReviewLogResponse {

    private Long id;
    private Long videoId;
    private Long reviewerId;
    private String beforeStatus;
    private String afterStatus;
    private String beforeResult;
    private String afterResult;
    private String comment;
    private LocalDateTime createdAt;

    public static ReviewLogResponse from(ReviewLog log) {
        ReviewLogResponse response = new ReviewLogResponse();
        response.setId(log.getId());
        response.setVideoId(log.getVideoId());
        response.setReviewerId(log.getReviewerId());
        response.setBeforeStatus(log.getBeforeStatus());
        response.setAfterStatus(log.getAfterStatus());
        response.setBeforeResult(log.getBeforeResult());
        response.setAfterResult(log.getAfterResult());
        response.setComment(log.getComment());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public String getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getBeforeResult() {
        return beforeResult;
    }

    public void setBeforeResult(String beforeResult) {
        this.beforeResult = beforeResult;
    }

    public String getAfterResult() {
        return afterResult;
    }

    public void setAfterResult(String afterResult) {
        this.afterResult = afterResult;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
