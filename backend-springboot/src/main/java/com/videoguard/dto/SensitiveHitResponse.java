package com.videoguard.dto;

import com.videoguard.entity.SensitiveHit;

public class SensitiveHitResponse {

    private Long id;
    private String sourceType;
    private String word;
    private String category;
    private Integer weight;
    private String contextText;

    public static SensitiveHitResponse from(SensitiveHit hit) {
        SensitiveHitResponse response = new SensitiveHitResponse();
        response.setId(hit.getId());
        response.setSourceType(hit.getSourceType());
        response.setWord(hit.getWord());
        response.setCategory(hit.getCategory());
        response.setWeight(hit.getWeight());
        response.setContextText(hit.getContextText());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

