package com.videoguard.controller;

import com.videoguard.dto.VideoDetailResponse;
import com.videoguard.dto.VideoListItemResponse;
import com.videoguard.dto.VideoUploadResponse;
import com.videoguard.service.VideoService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public VideoUploadResponse upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("uploaderId") @NotNull Long uploaderId) {
        return videoService.upload(file, title, description, uploaderId);
    }

    @GetMapping
    public List<VideoListItemResponse> list(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "aiRiskLevel", required = false) String aiRiskLevel) {
        return videoService.list(status, aiRiskLevel);
    }

    @GetMapping("/{id}")
    public VideoDetailResponse detail(@PathVariable Long id) {
        return videoService.detail(id);
    }

    @GetMapping("/{id}/play")
    public ResponseEntity<Void> playRedirect(@PathVariable Long id) {
        VideoDetailResponse detail = videoService.detail(id);
        return ResponseEntity.status(302)
                .header("Location", detail.getFileUrl())
                .build();
    }
}

