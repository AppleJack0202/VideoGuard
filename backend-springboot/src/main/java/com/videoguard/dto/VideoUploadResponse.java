package com.videoguard.dto;

import com.videoguard.entity.Video;

public class VideoUploadResponse {

    private Long videoId;
    private String title;
    private String filePath;
    private String fileUrl;
    private String status;

    public static VideoUploadResponse from(Video video) {
        VideoUploadResponse response = new VideoUploadResponse();
        response.setVideoId(video.getId());
        response.setTitle(video.getTitle());
        response.setFilePath(video.getFilePath());
        response.setFileUrl("/" + video.getFilePath().replace("\\", "/"));
        response.setStatus(video.getStatus());
        return response;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

