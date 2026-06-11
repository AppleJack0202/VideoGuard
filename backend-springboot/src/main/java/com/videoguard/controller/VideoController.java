package com.videoguard.controller;

import com.videoguard.dto.VideoDetailResponse;
import com.videoguard.dto.VideoListItemResponse;
import com.videoguard.dto.VideoUploadResponse;
import com.videoguard.service.VideoService;
import com.videoguard.service.WorkflowConstants;
import jakarta.servlet.http.HttpServletRequest;
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
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description) {
        return videoService.upload(file, title, description, currentUserId(request));
    }

    @GetMapping
    public List<VideoListItemResponse> list(
            HttpServletRequest request,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "aiRiskLevel", required = false) String aiRiskLevel,
            @RequestParam(value = "violationCategory", required = false) String violationCategory,
            @RequestParam(value = "mine", required = false, defaultValue = "false") boolean mine) {
        Long uploaderId = (mine || WorkflowConstants.ROLE_USER.equals(currentUserRole(request))) ? currentUserId(request) : null;
        List<VideoListItemResponse> videos = videoService.list(status, aiRiskLevel, violationCategory, uploaderId);
        if (WorkflowConstants.ROLE_USER.equals(currentUserRole(request))) {
            videos.forEach(this::hideAuditFields);
        }
        return videos;
    }

    @GetMapping("/{id}")
    public VideoDetailResponse detail(HttpServletRequest request, @PathVariable Long id) {
        if (WorkflowConstants.ROLE_USER.equals(currentUserRole(request))) {
            return videoService.userDetail(id, currentUserId(request));
        }
        return videoService.detail(id);
    }

    @PostMapping("/{id}/analyze")
    public VideoDetailResponse analyze(@PathVariable Long id) {
        return videoService.analyze(id);
    }

    @PostMapping("/{id}/asr/refresh")
    public VideoDetailResponse refreshAsr(@PathVariable Long id) {
        return videoService.refreshAsr(id);
    }

    @GetMapping("/{id}/play")
    public ResponseEntity<Void> playRedirect(HttpServletRequest request, @PathVariable Long id) {
        VideoDetailResponse detail = WorkflowConstants.ROLE_USER.equals(currentUserRole(request))
                ? videoService.userDetail(id, currentUserId(request))
                : videoService.detail(id);
        return ResponseEntity.status(302)
                .header("Location", detail.getFileUrl())
                .build();
    }

    private Long currentUserId(HttpServletRequest request) {
        Object value = request.getAttribute("currentUserId");
        if (value instanceof Long id) {
            return id;
        }
        throw new IllegalArgumentException("Current user is required.");
    }

    private String currentUserRole(HttpServletRequest request) {
        Object value = request.getAttribute("currentUserRole");
        return value instanceof String role ? role : WorkflowConstants.ROLE_USER;
    }

    private void hideAuditFields(VideoListItemResponse response) {
        response.setAiRiskLevel(null);
        response.setAiRiskScore(null);
        response.setViolationCategory(null);
    }
}
