package com.videoguard.dto;

import com.videoguard.entity.AiReviewResult;

public class AiReviewResultResponse {

    private Double textScore;
    private Double imageScore;
    private Double asrScore;
    private Double finalScore;
    private String riskLevel;
    private String asrText;

    public static AiReviewResultResponse from(AiReviewResult result) {
        if (result == null) {
            return null;
        }
        AiReviewResultResponse response = new AiReviewResultResponse();
        response.setTextScore(result.getTextScore());
        response.setImageScore(result.getImageScore());
        response.setAsrScore(result.getAsrScore());
        response.setFinalScore(result.getFinalScore());
        response.setRiskLevel(result.getRiskLevel());
        response.setAsrText(result.getAsrText());
        return response;
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
}

