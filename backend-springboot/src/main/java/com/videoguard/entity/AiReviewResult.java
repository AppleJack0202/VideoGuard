package com.videoguard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_review_result")
public class AiReviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "text_score")
    private Double textScore;

    @Column(name = "image_score")
    private Double imageScore;

    @Column(name = "asr_score")
    private Double asrScore;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "risk_level", nullable = false)
    private String riskLevel;

    @Column(name = "asr_text", columnDefinition = "MEDIUMTEXT")
    private String asrText;

    @Column(name = "raw_result_json", columnDefinition = "JSON")
    private String rawResultJson;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

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

    public Double getTextScore() {
        return textScore;
    }

    public void setTextScore(Double textScore) {
        this.textScore = textScore;
    }

    public Double getImageScore() {
        return imageScore;
    }

    public void setImageScore(Double imageScore) {
        this.imageScore = imageScore;
    }

    public Double getAsrScore() {
        return asrScore;
    }

    public void setAsrScore(Double asrScore) {
        this.asrScore = asrScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getAsrText() {
        return asrText;
    }

    public void setAsrText(String asrText) {
        this.asrText = asrText;
    }

    public String getRawResultJson() {
        return rawResultJson;
    }

    public void setRawResultJson(String rawResultJson) {
        this.rawResultJson = rawResultJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

