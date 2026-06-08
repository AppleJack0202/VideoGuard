package com.videoguard.dto;

import com.videoguard.entity.VideoFrame;

public class VideoFrameResponse {

    private Long id;
    private String framePath;
    private String frameUrl;
    private Double timestampSec;
    private String label;
    private Double confidence;
    private Double riskScore;

    public static VideoFrameResponse from(VideoFrame frame) {
        VideoFrameResponse response = new VideoFrameResponse();
        response.setId(frame.getId());
        response.setFramePath(frame.getFramePath());
        response.setFrameUrl("/" + frame.getFramePath().replace("\\", "/"));
        response.setTimestampSec(frame.getTimestampSec());
        response.setLabel(frame.getLabel());
        response.setConfidence(frame.getConfidence());
        response.setRiskScore(frame.getRiskScore());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFramePath() {
        return framePath;
    }

    public void setFramePath(String framePath) {
        this.framePath = framePath;
    }

    public String getFrameUrl() {
        return frameUrl;
    }

    public void setFrameUrl(String frameUrl) {
        this.frameUrl = frameUrl;
    }

    public Double getTimestampSec() {
        return timestampSec;
    }

    public void setTimestampSec(Double timestampSec) {
        this.timestampSec = timestampSec;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
}

