package com.videoguard.repository;

import com.videoguard.dto.CountItemResponse;
import com.videoguard.entity.SensitiveHit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SensitiveHitRepository extends JpaRepository<SensitiveHit, Long> {

    List<SensitiveHit> findByVideoIdOrderByCreatedAtAsc(Long videoId);

    void deleteByVideoId(Long videoId);

    void deleteByVideoIdAndSourceType(Long videoId, String sourceType);

    @Query("select new com.videoguard.dto.CountItemResponse(h.category, count(h)) "
            + "from SensitiveHit h group by h.category order by count(h) desc")
    List<CountItemResponse> countByCategory();
}
