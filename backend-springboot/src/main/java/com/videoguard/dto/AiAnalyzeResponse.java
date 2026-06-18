package com.videoguard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AiAnalyzeResponse {

    @JsonProperty("video_id")
    private Long videoId;
    private Metadata metadata;
    private List<FrameResult> frames;
    @JsonProperty("asr_text")
    private String asrText;
    @JsonProperty("text_hits")
    private List<TextHit> textHits;
    private Scores scores;
    @JsonProperty("risk_level")
    private String riskLevel;
    @JsonProperty("content_category")
    private ContentCategory contentCategory;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<FrameResult> getFrames() {
        return frames;
    }

    public void setFrames(List<FrameResult> frames) {
        this.frames = frames;
    }

    public String getAsrText() {
        return asrText;
    }

    public void setAsrText(String asrText) {
        this.asrText = asrText;
    }

    public List<TextHit> getTextHits() {
        return textHits;
    }

    public void setTextHits(List<TextHit> textHits) {
        this.textHits = textHits;
    }

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public ContentCategory getContentCategory() {
        return contentCategory;
    }

    public void setContentCategory(ContentCategory contentCategory) {
        this.contentCategory = contentCategory;
    }

    public static class ContentCategory {

        private String category;
        private Double confidence;
        private String reason;
        @JsonProperty("review_strategy")
        private String reviewStrategy;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getReviewStrategy() {
            return reviewStrategy;
        }

        public void setReviewStrategy(String reviewStrategy) {
            this.reviewStrategy = reviewStrategy;
        }
    }

    public static class Metadata {

        private Double duration;
        private Integer width;
        private Integer height;
        private Double fps;
        @JsonProperty("file_size")
        private Long fileSize;

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

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }
    }

    public static class FrameResult {

        @JsonProperty("frame_path")
        private String framePath;
        @JsonProperty("timestamp_sec")
        private Double timestampSec;
        private String label;
        private Double confidence;
        @JsonProperty("risk_score")
        private Double riskScore;

        public String getFramePath() {
            return framePath;
        }

        public void setFramePath(String framePath) {
            this.framePath = framePath;
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

    public static class TextHit {

        @JsonProperty("source_type")
        private String sourceType;
        private String word;
        private String category;
        private Integer weight;
        @JsonProperty("context_text")
        private String contextText;

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public String getContextText() {
            return contextText;
        }

        public void setContextText(String contextText) {
            this.contextText = contextText;
        }
    }

    public static class Scores {

        @JsonProperty("text_score")
        private Double textScore;
        @JsonProperty("image_score")
        private Double imageScore;
        @JsonProperty("asr_score")
        private Double asrScore;
        @JsonProperty("final_score")
        private Double finalScore;

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
    }

    public record SensitiveWordPayload(String word, String category, Integer weight) {
    }

    public record AnalyzeRequestPayload(
            @JsonProperty("video_id") Long videoId,
            @JsonProperty("video_path") String videoPath,
            String title,
            String description,
            @JsonProperty("frame_interval_sec") Integer frameIntervalSec,
            @JsonProperty("sensitive_words") List<SensitiveWordPayload> sensitiveWords) {
    }

    public Map<String, Object> toSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("videoId", videoId);
        summary.put("riskLevel", riskLevel);
        if (contentCategory != null) {
            Map<String, Object> categoryMap = new LinkedHashMap<>();
            categoryMap.put("category", contentCategory.getCategory());
            categoryMap.put("confidence", contentCategory.getConfidence());
            categoryMap.put("reviewStrategy", contentCategory.getReviewStrategy());
            summary.put("contentCategory", categoryMap);
        }
        if (scores != null) {
            Map<String, Object> scoreMap = new LinkedHashMap<>();
            scoreMap.put("textScore", scores.getTextScore());
            scoreMap.put("imageScore", scores.getImageScore());
            scoreMap.put("asrScore", scores.getAsrScore());
            scoreMap.put("finalScore", scores.getFinalScore());
            summary.put("scores", scoreMap);
        } else {
            summary.put("scores", Map.of());
        }
        return summary;
    }
}
