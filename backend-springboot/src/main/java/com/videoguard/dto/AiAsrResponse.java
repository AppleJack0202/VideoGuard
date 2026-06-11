package com.videoguard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiAsrResponse {

    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("asr_text")
    private String asrText;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getAsrText() {
        return asrText;
    }

    public void setAsrText(String asrText) {
        this.asrText = asrText;
    }
}
