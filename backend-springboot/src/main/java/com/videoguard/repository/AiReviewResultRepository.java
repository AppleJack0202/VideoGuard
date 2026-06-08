package com.videoguard.repository;

import com.videoguard.entity.AiReviewResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiReviewResultRepository extends JpaRepository<AiReviewResult, Long> {

    Optional<AiReviewResult> findTopByVideoIdOrderByCreatedAtDesc(Long videoId);

    void deleteByVideoId(Long videoId);
}

