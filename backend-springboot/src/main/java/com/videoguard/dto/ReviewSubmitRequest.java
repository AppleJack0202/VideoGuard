package com.videoguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewSubmitRequest {

    @NotNull
    private Long reviewerId;

    @NotBlank
    private String status;

    private String violationCategory;

    @NotBlank
    private String comment;

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(String violationCategory) {
        this.violationCategory = violationCategory;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
