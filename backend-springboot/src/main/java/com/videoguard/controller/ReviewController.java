package com.videoguard.controller;

import com.videoguard.dto.ReviewLogResponse;
import com.videoguard.dto.ReviewSubmitRequest;
import com.videoguard.dto.VideoDetailResponse;
import com.videoguard.dto.VideoListItemResponse;
import com.videoguard.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/tasks")
    public List<VideoListItemResponse> tasks(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "aiRiskLevel", required = false) String aiRiskLevel,
            @RequestParam(value = "violationCategory", required = false) String violationCategory) {
        return reviewService.tasks(status, aiRiskLevel, violationCategory);
    }

    @GetMapping("/tasks/{videoId}")
    public VideoDetailResponse taskDetail(@PathVariable Long videoId) {
        return reviewService.taskDetail(videoId);
    }

    @PostMapping("/tasks/{videoId}/submit")
    public VideoDetailResponse submit(@PathVariable Long videoId, @Valid @RequestBody ReviewSubmitRequest request) {
        return reviewService.submit(videoId, request);
    }

    @GetMapping("/logs/{videoId}")
    public List<ReviewLogResponse> logs(@PathVariable Long videoId) {
        return reviewService.logs(videoId);
    }
}
