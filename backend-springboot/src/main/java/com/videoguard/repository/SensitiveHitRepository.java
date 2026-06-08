package com.videoguard.repository;

import com.videoguard.entity.SensitiveHit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensitiveHitRepository extends JpaRepository<SensitiveHit, Long> {

    List<SensitiveHit> findByVideoIdOrderByCreatedAtAsc(Long videoId);

    void deleteByVideoId(Long videoId);
}

