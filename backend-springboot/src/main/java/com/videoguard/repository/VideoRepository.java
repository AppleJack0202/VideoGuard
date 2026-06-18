package com.videoguard.repository;

import com.videoguard.dto.CountItemResponse;
import com.videoguard.entity.Video;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {

    long countByCreatedAtGreaterThanEqual(LocalDateTime createdAt);

    long countByStatusInAndFinalResultIsNull(Collection<String> statuses);

    long countByStatusIn(Collection<String> statuses);

    long countByStatus(String status);

    List<Video> findByStatusOrderByCreatedAtAsc(String status);

    long countByAiRiskLevel(String aiRiskLevel);

    long countByAiRiskLevelIsNotNull();

    long countByStatusInAndAiRiskLevel(Collection<String> statuses, String aiRiskLevel);

    @Query("select new com.videoguard.dto.CountItemResponse(coalesce(v.aiRiskLevel, 'UNANALYZED'), count(v)) "
            + "from Video v group by coalesce(v.aiRiskLevel, 'UNANALYZED')")
    List<CountItemResponse> countByRiskLevel();

    @Query("select new com.videoguard.dto.CountItemResponse(v.status, count(v)) "
            + "from Video v group by v.status")
    List<CountItemResponse> countByStatusGroup();

    @Query("select new com.videoguard.dto.CountItemResponse(coalesce(v.violationCategory, '未分类'), count(v)) "
            + "from Video v where v.violationCategory is not null group by coalesce(v.violationCategory, '未分类')")
    List<CountItemResponse> countByViolationCategory();

    @Query("select new com.videoguard.dto.CountItemResponse("
            + "cast(function('date_format', v.createdAt, '%Y-%m-%d') as string), count(v)) "
            + "from Video v where v.createdAt >= :start "
            + "group by cast(function('date_format', v.createdAt, '%Y-%m-%d') as string) "
            + "order by cast(function('date_format', v.createdAt, '%Y-%m-%d') as string)")
    List<CountItemResponse> countDailyUploads(LocalDateTime start);
}
