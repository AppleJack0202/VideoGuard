package com.videoguard.dto;

import com.videoguard.entity.SensitiveWord;
import java.time.LocalDateTime;

public class SensitiveWordResponse {

    private Long id;
    private String word;
    private String category;
    private Integer weight;
    private Integer enabled;
    private LocalDateTime createdAt;

    public static SensitiveWordResponse from(SensitiveWord sensitiveWord) {
        SensitiveWordResponse response = new SensitiveWordResponse();
        response.setId(sensitiveWord.getId());
        response.setWord(sensitiveWord.getWord());
        response.setCategory(sensitiveWord.getCategory());
        response.setWeight(sensitiveWord.getWeight());
        response.setEnabled(sensitiveWord.getEnabled());
        response.setCreatedAt(sensitiveWord.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
