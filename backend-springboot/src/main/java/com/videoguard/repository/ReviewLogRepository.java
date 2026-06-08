package com.videoguard.repository;

import com.videoguard.entity.ReviewLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {

    List<ReviewLog> findByVideoIdOrderByCreatedAtDesc(Long videoId);
}
