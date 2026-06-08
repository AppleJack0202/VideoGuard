package com.videoguard.repository;

import com.videoguard.entity.VideoFrame;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoFrameRepository extends JpaRepository<VideoFrame, Long> {

    List<VideoFrame> findByVideoIdOrderByTimestampSecAsc(Long videoId);

    void deleteByVideoId(Long videoId);
}

